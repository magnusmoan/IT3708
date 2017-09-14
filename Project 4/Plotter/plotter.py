import matplotlib.pyplot as plt
import plotly
import plotly.figure_factory as ff
from sys import argv

algorithm = argv[1]
problem_number = argv[2]
filename = "../Results/txt/" + algorithm + "_" + problem_number + ".res"

f = open(filename, 'r')
df = []

header= [int(s) for s in f.readline().split(' ')]
n_activities = header[0]
makespan = header[1]

colormap = plt.cm.Vega20
title = algorithm.upper() + ": Problem " + problem_number + " (Makespan: " + str(makespan) + ")"

for line in f:
    line = line.rstrip()
    info = [s for s in line.split(' ')]

    if(int(info[2]) < 10):
        start = "000" + info[2]
    elif(int(info[2]) < 100):
        start = "00" + info[2]
    elif(int(info[2]) < 1000):
        start = "0" + info[2]
    else:
        start = info[2]

    if(int(info[3]) < 10):
        finish = "000" + info[3]
    elif(int(info[3]) < 100):
        finish = "00" + info[3]
    elif(int(info[3]) < 1000):
        finish = "0" + info[3]
    else:
        finish = info[3]
    
    df.append(dict(Task="M"+info[0], Start=start, Finish=finish, Resource="Job-" + str(1+int(info[1]))))

colors = {}

if(n_activities <= len(colormap.colors)):
    for i in range(n_activities):
        colors['Job-' + str(i+1)] = colormap.colors[i]
else:
    for i in range(len(colormap.colors)):
        colors['Job-' + str(i+1)] = colormap.colors[i]
    sndColormap = plt.cm.Set1
    for i in range(len(colormap.colors), n_activities - len(colormap.colors)):
        colors['Job-' + str(i+1)] = sndColormap.colors[i]

fig = ff.create_gantt(df, title=title, colors=colors, index_col='Resource', show_colorbar=True, group_tasks=True)
plotly.offline.plot(fig, filename="../Results/HTML/" + algorithm + "_" + problem_number + ".html", auto_open=True)
plotly.plotly.image.save_as(fig, filename="../Results/PNG/" + algorithm + "_" + problem_number + ".png")
f.close()
