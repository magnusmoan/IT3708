from matplotlib import *
import pylab
from mpl_toolkits.mplot3d import Axes3D

def plot3d(filename):
    fig = pylab.figure()
    ax = Axes3D(fig)

    content = []
    f = open('../Pareto/' + filename, 'r')
    for line in f:
        content.append(line.split())

    for i in range(0, len(content)):
        content[i] = [float(x) for x in content[i]]

    for i in range(0, len(content)):
        content[i] = [int(content[i][0])] + content[i][1:]

    x = []
    y = []
    z = []
    labels = []

    for i in range(0, len(content)):
        x.append(content[i][1])
        y.append(content[i][2])
        z.append(content[i][3])
        labels.append(str(content[i][0]))

    for i in range(len(content)):
        ax.scatter(x[i],y[i],z[i],color='b') 

    pylab.xlabel('Overall Deviation')
    pylab.ylabel('Edge value')
    ax.set_zlabel('Connectivity')
    pylab.locator_params(nticks=2)

    pylab.savefig("../Images/plots/all")
