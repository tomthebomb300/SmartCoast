import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from sklearn.cluster import KMeans
import sys


def elbowMethod():
    distortions = []
    
    for k in range(1,10):
        kmeans = KMeans(n_clusters=k)
        kmeans.fit(dataframe.values)
        distortions.append(kmeans.inertia_)
    
    plt.figure()
    plt.plot(range(1, 10), distortions, 'bx-')
    plt.xlabel('k')
    plt.ylabel('distortion')
    plt.title('elbow method')
    plt.show()
    
def main(file):
    dataframe = pd.read_csv(file)
    k = 3
    kmeans = KMeans(n_clusters=3)
    kmeans.fit(dataframe.values)
    dataframe['cluster'] = kmeans.labels_
    dataframe.to_csv(file, index=False)



if __name__ == '__main__':
    if(len(sys.argv)>1):
        main(sys.argv[1])