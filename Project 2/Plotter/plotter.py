import matplotlib.pyplot as plt
import numpy as np

def draw_graph(customer_coordinates, depot_coordinates, truck_routes, x_max, y_max, x_min, y_min, length, plot_name):

    fig, ax = plt.subplots(num=None, figsize=(10,10), dpi=100)
    fig.canvas.set_window_title('IT3708: Project 2')
    colormap = plt.cm.Set1
    plt.gca().set_color_cycle([colormap(i) for i in np.linspace(0, 0.9, 10)])

    for depot in range(len(truck_routes)):
        curr_depot = depot_coordinates[depot]

        for truck_route in range(len(truck_routes[depot])):
            route = [curr_depot]
            current_truck_route = truck_routes[depot][truck_route]
            
            for customer in current_truck_route:
                route.append(customer_coordinates[customer-1])

            route.append(curr_depot)
            plt.plot(*zip(*route), linewidth=2.0, zorder=-1)

    customers = plt.scatter(*zip(*customer_coordinates), c='#00ABFF', label='Customers', s=75, zorder=1)
    depots = plt.scatter(*zip(*depot_coordinates), c='#FFBA00', label='Depots', s=100, zorder=2)

    plt.axis([x_min, x_max, y_min, y_max])
    plt.title("Data set: " + plot_name + "(Length: " + length + ")", position=(.5,1.05))
    plt.legend(bbox_to_anchor=(1,1.12))

    plt.show()
