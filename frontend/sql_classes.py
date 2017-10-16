'''
Author: Josue Caraballo
Description: Houses SQLAlchemy class for connecting to MySQL instance
'''
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String

Base = declarative_base()
class Sample(Base):
    __tablename__ = "samples"
    sample_no = Column(Integer, primary_key=True, nullable=False)
    word = Column(String(255), primary_key=True, nullable=False)
    file_path = Column(String(9999), nullable=False)
    def __repr__(self):
        return self.__str__()
    def __str__(self):
        return "<Sample (number:{}, word:{}, file_path:{})>"\
                .format(self.sample_no, self.word, self.file_path)
