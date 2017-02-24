import numpy as np
import os
from operator import attrgetter
from config import Config
from solver import Solver, SolverConfig

from antonio.solution import Solution

INPUTS_PATH = "inputs"
OUTPUTS_PATH = "outputs"

INPUT_FILES = ["kittens", "me_at_the_zoo", "trending_today", "videos_worth_spreading"]

EXAMPLE = 1
SMALL = 3
MEDIUM = 2
BIG = 0

CURRENT = EXAMPLE

if __name__ == "__main__":
    config_filename = os.path.join(INPUTS_PATH, INPUT_FILES[CURRENT] + ".in")
    config = Config(config_filename)

    if config.valid:
        solvers = []
        solver_configs = []
        solver_configs.append(SolverConfig("Test"))
        for solver_config in solver_configs:
            solvers.append(Solver(config, solver_config))
        for solver in solvers:
            solver.solve()
        solution = Solution()

        # initialization of the parameters:
        # alpha -> number of elements that are analyzed
        # beta  -> 1st weight
        # gamma -> 2nd weight
        alpha = 10
        beta = 0.1
        gamma = 0.8

        # initialization of the cache free space
        cache_free_space_list = np.repeat(config.capacity, config.nb_caches)

        # put the information of all the request_description in separated lists
        list_endpoints_id = [x.endpoint_id for x in config.request_descriptions]
        list_nb_requests = [x.nb_requests for x in config.request_descriptions]
        list_requested_video_id = [x.requested_video_id for x in config.request_descriptions]
        list_nb_requests, list_endpoints_id, list_requested_video_id = zip(*sorted(zip(list_nb_requests, list_endpoints_id, list_requested_video_id), reverse=True))

        list_nb_requests = list(list_nb_requests)
        list_endpoints_id = list(list_endpoints_id)
        list_requested_video_id = list(list_requested_video_id)
        list_video_size = []
        for i in range(len(list_requested_video_id)):
            list_video_size.append(config.video_sizes[list_requested_video_id[i]])

        # the while loop will be break
        while True:

            print(len(list_requested_video_id))

            # calculate the video_size_max parameter
            video_size_max = max(list_video_size)

            # choose the best video by comparing a
            weight_video_score_list = []
            weight_video_id_list = []
            for i in range(0, min([alpha, len(list_endpoints_id)])):
                endpoint_id = list_endpoints_id[i]
                video_id = list_requested_video_id[i]
                video_size = config.video_sizes[video_id]
                endpoint_latency = config.endpoints[endpoint_id].latency
                endpoint_connections = config.endpoints[endpoint_id].connections

                # chose the best cache
                weight_cache_score_list = []
                weight_cache_id_list = []
                for j in endpoint_connections.keys():
                    cache_id = j
                    cache_latency = endpoint_connections[j]
                    if video_size <= cache_free_space_list[j]:
                        score_cache = (1-beta)*(endpoint_latency-cache_latency)/endpoint_latency + \
                            beta*(config.capacity-cache_free_space_list[j]/config.capacity)
                    else:
                        score_cache = -1
                    weight_cache_score_list.append(score_cache)
                    weight_cache_id_list.append(cache_id)

                # it could happen that endpoints has no connection:
                # just skip the line
                if len(weight_cache_score_list) == 0:
                    break

                best_cache = weight_cache_id_list[weight_cache_score_list.index(max(weight_cache_score_list))]

                # chose the best video
                best_cache_latency = endpoint_connections[best_cache]
                if max(weight_cache_score_list) != -1:
                    score_video = (1-gamma)*(endpoint_latency-best_cache_latency)/endpoint_latency + \
                        gamma*(video_size_max-video_size/video_size_max)
                else:
                    score_video = -1
                weight_video_score_list.append(score_video)
                weight_video_id_list.append(best_cache)

            if len(weight_video_score_list) == 0:
                break

            best_video_val_index = weight_video_score_list.index(max(weight_video_score_list))
            best_video_id = list_requested_video_id[best_video_val_index]
            best_cache_id = weight_video_id_list[best_video_val_index]
            best_video_size = config.video_sizes[best_video_id]

            # remove the -1
            worst_video_val_index = [i for i, val in enumerate(weight_video_score_list) if val == -1]
            #worst_video_val_index.append(best_video_val_index)
            if len(worst_video_val_index):
                for index in sorted(worst_video_val_index, reverse=True):
                    del list_endpoints_id[index]
                    del list_nb_requests[index]
                    del list_requested_video_id[index]
                    del list_video_size[index]

            # remove the same video
            same_video_index = [i for i, val in enumerate(list_requested_video_id) if val == best_video_id]
            for index in sorted(same_video_index, reverse=True):
                del list_endpoints_id[index]
                del list_nb_requests[index]
                del list_requested_video_id[index]
                del list_video_size[index]


            # update the cache
            cache_free_space_list[best_cache_id] -= best_video_size

            # if all elements are equal to -1
            if weight_video_score_list.count(-1) == len(weight_video_score_list):
                if alpha < len(list_requested_video_id):
                    alpha = min([2*alpha, len(list_requested_video_id)])
                else:
                    break
            else:
                if best_cache_id not in solution.cache_servers:
                    solution.cache_servers[best_cache_id] = []
                solution.cache_servers[best_cache_id].append(best_video_id)

            if len(list_requested_video_id) == 0:
                break

        solution.write_result("output")
        print(solution.compute_score(config))

        best_solution = max(solvers, key=attrgetter('score'))
        print("Best solver: {} ({})".format(best_solution.solver_config.name, best_solution.score))