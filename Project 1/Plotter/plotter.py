import matplotlib.patches as mpatches
import matplotlib.pyplot as plt

for file_name in ["Supervised", "Reinforcementv1", "Reinforcementv2", "Baseline"]:
    with open("../Results/" + file_name + "-results") as f:
        values = f.readlines()

    values = [x.strip() for x in values]
    if len(values) > 0:
        agent_type = values[0]
        values = values[1:]

        plt.plot([i for i in range(1, len(values)+1)], values, lw=2, label=file_name)

plt.axis([0, len(values)+1, 0, 30])
plt.xlabel("Round number (100 games per round)")
plt.ylabel("Average score")
plt.legend(bbox_to_anchor=(0.55, 0.35), loc=2, borderaxespad=0.)

plt.savefig("../png/results.png")
