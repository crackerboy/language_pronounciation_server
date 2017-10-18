'''
Author: Josue Caraballo
Description: Runs feature extraction on input audio, as well as all matching local audio files.
Subsequently aggregates all local audio file features into a 'mean' feature matrix and compares this with input audio feature matrix.

Disclaimer: Heavily influenced by tutorial at https://librosa.github.io/librosa/tutorial.html

To-DO: SAVE recieved file with timestamp in tmp folder, use the timestamp as the filename.
'''
import librosa, math
import numpy as np
from itertools import starmap

_HOP_LENGTH_ = 512

def extract_features(y, sr):
    '''
    Function to extract features from audio.
    '''
    # Separate harmonics and percussives into two waveforms
    y_harmonic, y_percussive = librosa.effects.hpss(y)
    # Beat track on the percussive signal
    tempo, beat_frames = librosa.beat.beat_track(y=y_percussive,sr=sr)
    # Compute MFCC features from the raw signal
    mfcc = librosa.feature.mfcc(y=y, sr=sr, hop_length=_HOP_LENGTH_, n_mfcc=13)
    # And the first-order differences (delta features)
    mfcc_delta = librosa.feature.delta(mfcc)
    # Stack and synchronize between beat events
    # This time, we'll use the mean value (default) instead of median
    beat_mfcc_delta = librosa.util.sync(np.vstack([mfcc, mfcc_delta]), beat_frames)
    # Compute chroma features from the harmonic signal
    chromagram = librosa.feature.chroma_cqt(y=y_harmonic, sr=sr)
    # Aggregate chroma features between beat events
    # We'll use the median value of each feature between beat frames
    beat_chroma = librosa.util.sync(chromagram, beat_frames, aggregate=np.median)
    # Finally, stack all beat-synchronous features together
    beat_features = np.vstack([beat_chroma, beat_mfcc_delta])
    return beat_features

def normalize(vector_a, vector_b):
    '''
    Function should normalize vectors relative to each other. (sum of a row is 1)
    Does this by concatenating to matrix, normalizing, then splitting back into vectors
    '''
    matrix = np.column_stack((vector_a, vector_b))
    row_sums = matrix.sum(axis=1)
    matrix_norm = matrix/row_sums[:,np.newaxis]
    a_norm, b_norm = np.split(matrix_norm,2,axis=1)
    return a_norm, b_norm

def pipeline(input_file_path, files_to_compare):
    # Extracting features
    in_y, in_sr = librosa.load(input_file_path)
    local_data = [(y,sr) for y,sr in map(librosa.load, files_to_compare)]
    in_features = extract_features(in_y, in_sr)
    local_features = [features for features in starmap(extract_features, local_data)]

    # Aggregating local features
    local_feature_aggregate = [features.mean(1) for features in local_features]
    local_feature_aggregate = np.array(local_feature_aggregate).mean(0)

    # Aggregating input features
    in_feature_aggregate = in_features.mean(1)

    # Working on comparing aggregates, perhaps normalizing both vectors with the same method will help?
    local_feature_aggregate, in_feature_aggregate = normalize(local_feature_aggregate, in_feature_aggregate)

    mean_difference = math.fabs(np.mean(local_feature_aggregate - in_feature_aggregate))
    mean_similarity = 1 - mean_difference
    return mean_similarity
