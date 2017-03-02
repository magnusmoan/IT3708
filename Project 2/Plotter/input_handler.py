import copy as cp

def read_file(filename):
    with open(filename) as f:
        content = f.readlines()
    return content

def get_truck_routes(filename):
    content = [line.strip() for line in read_file(filename)]

    length = content[0]
    content = content[1:]
    truck_routes_flat = []

    for line in range(len(content)-1):
        curr_line = content[line]
        depot_number = int(curr_line[0])
        route = map(int, curr_line[21:].split())
        route = route[1:-1]
        truck_routes_flat.append([depot_number, route])

    truck_routes_hierarchy = []
    curr_depot = truck_routes_flat[0][0]
    depot_list = []
    for truck in truck_routes_flat:
        depot = truck[0]
        if depot != curr_depot:
            truck_routes_hierarchy.append(cp.deepcopy(depot_list))
            depot_list = []
            curr_depot = depot
        depot_list.append(truck[1])

    truck_routes_hierarchy.append(depot_list)
    
    return truck_routes_hierarchy, length

def get_coordinates(filename):
    content = [line.strip() for line in read_file(filename)]

    no_of_customers = map(int, content[0].split(' '))[1]
    no_of_depots = map(int, content[0].split(' '))[2]

    customer_coord = []
    customer_end_index = no_of_customers+no_of_depots+1

    for customer in range(no_of_depots+1, customer_end_index):
        curr_customer_info = map(int, content[customer].split())
        customer_coord.append((curr_customer_info[1], curr_customer_info[2]))


    depot_coord = []
    for depot in range(customer_end_index, customer_end_index+no_of_depots):
        curr_depot_info = map(int, content[depot].split())
        depot_coord.append((curr_depot_info[1], curr_depot_info[2]))

    return customer_coord, depot_coord
