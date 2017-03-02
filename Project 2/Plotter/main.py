from input_handler import get_truck_routes, get_coordinates
from plotter import draw_graph
from sys import argv


def plot():
    data_folder = '../Data/Data Files/'

    if testing:
        result_folder = '../Data/Solution Files/'
    else:
        result_folder = '../Data/Results/'

    solutions_filename = filename + '.res'

    customer_coord, depot_coord = get_coordinates(data_folder + filename)
    truck_routes, length = get_truck_routes(result_folder + solutions_filename)


    x_max = max(max(zip(*customer_coord)[0])+10, max(zip(*depot_coord)[0])+10)
    y_max = max(max(zip(*customer_coord)[1])+10, max(zip(*depot_coord)[1])+10)
    x_min = min(min(zip(*customer_coord)[0])-10, min(zip(*depot_coord)[0])-10,0)
    y_min = min(min(zip(*customer_coord)[1])-10, min(zip(*depot_coord)[1])-10,0)

    draw_graph(customer_coord, depot_coord, truck_routes, x_max, y_max, x_min, y_min, length, filename)


if(len(argv) < 2):
    print "Expected atleast 2 command line arguments."
else:
    if(argv[1] == 'f'):
        testing = False
    else:
        testing = True

    if(len(argv) == 3):
        filename = argv[2]
    else:
        filename = 'p01'
    plot()

