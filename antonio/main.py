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

CURRENT = BIG

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
        alp = 10
        bet = 0.1
        gam = 0.9
        cache_space_list = np.repeat(config.capacity, config.nb_caches)

        req_dec = config.request_descriptions

        list_endpoints = [x.endpoint_id for x in req_dec]
        list_nb_requests = [x.nb_requests for x in req_dec]
        list_requested_video_id = [x.requested_video_id for x in req_dec]
        list_nb_requests, list_endpoints, list_requested_video_id = zip(*sorted(zip(list_nb_requests, list_endpoints,list_requested_video_id)))

        loop = True
        while loop:

            video_size_max = 0
            for i in range(len(list_requested_video_id)):
                video_id = list_requested_video_id[i]
                video_size = config.video_sizes[video_id]
                if (video_size > video_size_max):
                    video_size_max = video_size

            print(len(list_requested_video_id))

            func2_res = []
            func2_id = []
            # chose best video
            for i in range(min([alp, len(list_endpoints)])):
                endpoint_id = list_endpoints[i]
                video_id = list_requested_video_id[i]
                video_size = config.video_sizes[video_id]
                endpoint_latency = config.endpoints[endpoint_id].latency
                endpoint_connections = config.endpoints[endpoint_id].connections

                # chose the best cache
                func1_res = []
                func1_id = []
                for j in endpoint_connections.keys():
                    cache_id = j
                    cache_latency = endpoint_connections[j]
                    if (video_size <= cache_space_list[j]):
                        func1 = (1-bet)*(endpoint_latency-cache_latency)/endpoint_latency + \
                            bet*(config.capacity-cache_space_list[j]/config.capacity)
                    else:
                        func1 = -1
                    func1_res.append(func1)
                    func1_id.append(cache_id)

                best_cache = func1_id[func1_res.index(max(func1_res))]

                # chose the best video
                best_cache_latency = endpoint_connections[best_cache]
                if max(func1_res) != -1:
                    func2 = (1-gam)*(endpoint_latency-best_cache_latency)/endpoint_latency + \
                        gam*(video_size_max-video_size/video_size_max)
                else:
                    func2 = -1
                func2_res.append(func2)
                func2_id.append(best_cache)

            if len(func2_res) == 0:
                break

            best_video_val_i = func2_res.index(max(func2_res))
            best_video_id = list_requested_video_id[best_video_val_i]
            best_cache_id = func2_id[best_video_val_i]
            best_video_size = config.video_sizes[best_video_id]

            # update the cache
            cache_space_list[best_cache_id] -= best_video_size
            list_endpoints = list(filter(lambda a: a != best_video_id, list_endpoints))
            list_nb_requests = list(filter(lambda a: a != best_video_id, list_nb_requests))
            list_requested_video_id = list(filter(lambda a: a != best_video_id, list_requested_video_id))

            all_moins_un = True
            for entry in func2_res:
                if entry != -1:
                    all_moins_un = False
                    break

            if all_moins_un:
                break

            if best_cache_id not in solution.cache_servers:
                solution.cache_servers[best_cache_id] = []
            solution.cache_servers[best_cache_id].append(best_video_id)




        solution.write_result("output")
        print(solution.compute_score(config))

        best_solution = max(solvers, key=attrgetter('score'))
        print("Best solver: {} ({})".format(best_solution.solver_config.name, best_solution.score))