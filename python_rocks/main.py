import os

from operator import attrgetter
import numpy as np


from config import Config
from solution import Solution
from solver import Solver, SolverConfig

INPUTS_PATH = "inputs"
OUTPUTS_PATH = "outputs"

CONF_NAMES = ["kittens", "me_at_the_zoo", "trending_today", "videos_worth_spreading"]

EXAMPLE = 1
SMALL = 3
MEDIUM = 2
BIG = 0

CONFS = [EXAMPLE, SMALL, MEDIUM, BIG]

if __name__ == "__main__":
    for conf_id in CONFS:
        print("Config : {}".format(CONF_NAMES[conf_id]))
        config_filename = os.path.join(INPUTS_PATH, CONF_NAMES[conf_id] + ".in")
        config = Config(config_filename)

        if config.valid:
            solvers = []
            solver_configs = []

            alphas = [2, 5, 10, 15, 20, 25, 30]
            betas = [0.1, 0.3, 0.5, 0.8]
            gamas = [0.1, 0.3, 0.5, 0.8]

            _bgs = [(0.1, 0.8,),
                  (0.2, 0.8,),
                  (0.3, 0.8,),
                  (0.8, 0.1,),
                  (0.8, 0.2,),
                  (0.8, 0.3,)]

            for a in alphas:
                for bg in _bgs:
                    b = bg[0]
                    g = bg[1]
                    sconf = SolverConfig("Test alpha={}, beta={}, gamma={}".format(str(a), str(b), str(g)))
                    sconf.alpha = a
                    sconf.beta = b
                    sconf.gama = g
                    solver_configs.append(sconf)

            for solver_config in solver_configs:
                solvers.append(Solver(config, solver_config))

            for solver in solvers:
                solver.solve()

            best_solution = max(solvers, key=attrgetter('score'))
            print("Best solver: {} ({})".format(best_solution.solver_config.name, int(best_solution.score)))

            sol = best_solution.best_solution
            print()
            out_filename = os.path.join(OUTPUTS_PATH, CONF_NAMES[conf_id] + ".out")
            sol.write_result(out_filename)