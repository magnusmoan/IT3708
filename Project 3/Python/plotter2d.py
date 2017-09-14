import matplotlib.pyplot as plt
import numpy as np

def plot2d(xAxis, yAxis, x, y):
    plt.scatter(x, y)
    plt.axis([min(x)-5000, max(x)+5000, min(y)-5000, max(y)+5000])
    plt.xlabel(xAxis)
    plt.ylabel(yAxis)
    plt.title("Pareto front: " + xAxis + " vs. " + yAxis)

    plt.savefig("../Images/plots/" + xAxis + "_" + yAxis)
    plt.close()
