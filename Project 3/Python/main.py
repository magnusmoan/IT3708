from plotter2d import plot2d
from plotter3d import plot3d
from sys import argv
import os


filename = argv[1]
deviation = argv[2]
edge = argv[3]
conn = argv[4]

if(deviation == '1' and edge == '1' and conn == '1'):
    plot3d(filename)
else:
    f = open("../Pareto/" + filename, 'r')
    content = []
    for line in f:
        content.append(line.split())

    for i in range(0, len(content)):
        content[i] = [float(x) for x in content[i]]

    for i in range(0, len(content)):
        content[i] = [int(content[i][0])] + content[i][1:]

    x = []
    y = []
    z = []
    
    for i in range(0, len(content)):
        x.append(content[i][1])
        y.append(content[i][2])
        z.append(content[i][3])

    if(deviation != '1'):
        plot2d("Connectivity", "Edge", z, y)
    else:
        if(edge == '1'):
            plot2d("Deviation", "Edge", x, y)
        else:
            plot2d("Deviation", "Connectivity", x, z)
        
