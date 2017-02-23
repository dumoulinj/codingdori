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


        alp = 10
        bet = 0.5
        gam = 0.5
        cache_space_list = np.repeat(config.capacity, config.nb_caches)

        req_dec = config.request_descriptions
        list_req = np.empty([0, 3], dtype=int)
        for i in range(0,len(req_dec)):
            list_req = np.vstack([list_req,[req_dec[i].endpoint_id,req_dec[i].nb_requests,req_dec[i].requested_video_id]])
        list_req.view('i8,i8,i8').sort(order=['f1'], axis=0)
        list_req = np.flipud(list_req)

        list_req_sub = list_req[0:alp,:]
        # print(list_req_sub)

        video_size_max = 0
        for i in range(len(list_req_sub)):
            video_id = list_req_sub[i][2]
            video_size = config.video_sizes[video_id]
            if (video_size > video_size_max):
                video_size_max = video_size
        print(video_size_max)

        func2_res = []
        func2_id = []
        # chose one endpoint per line
        for i in range(len(list_req_sub)):
            endpoint_id = list_req_sub[i][0]
            video_id = list_req_sub[i][2]
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
            best_cache = func1_res.index(max(func1_res))

            # chose the best video
            best_cache_latency = endpoint_connections[func1_id[best_cache]]

            func2 = (1-gam)*(endpoint_latency-best_cache_latency)/endpoint_latency + \
                    gam*(video_size_max-video_size/video_size_max)
            func2_res.append(func2)
            func2_id.append(func1_id[best_cache])

        best_video_i = func2_res.index(max(func2_res))
        best_video_id = list_req_sub[best_video_i][2]
        best_cache_id = func2_id[best_video_i]

        solution = Solution()
        solution.cache_servers[best_cache_id].append(best_video_id)




        # solution.write_result("output")
        # print(solution.compute_score())

        best_solution = max(solvers, key=attrgetter('score'))
        print("Best solver: {} ({})".format(best_solution.solver_config.name, best_solution.score))