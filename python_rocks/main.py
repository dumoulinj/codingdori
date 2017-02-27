import os

from operator import attrgetter
import numpy as np
import pickle

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
#CONFS = [MEDIUM]


LOAD_CONF = False

if __name__ == "__main__":
    for conf_id in CONFS:
        print("Config : {}".format(CONF_NAMES[conf_id]))
        config_filename = os.path.join(INPUTS_PATH, CONF_NAMES[conf_id] + ".in")

        pkl_config_filename = config_filename + ".pkl"
        if not LOAD_CONF:
            config = Config(config_filename)

            # Compute possible gain by video for each endpoints
            config.read_configuration()
            config.compute_gains_by_video()

            # Pickle gains
            output = open(pkl_config_filename, 'wb')
            pickle.dump(config, output)
            output.close()
        else:
            pkl_file = open(pkl_config_filename, 'rb')
            config = pickle.load(pkl_file)
            print("Config loaded!")

        if config.valid:
            solvers = []
            solver_configs = []

            alphas = [10, 15, 20, 25]
            betas = gammas = [0, 1., 10., 100.]

            #gammas = [1., 10., 100., 1000.]

            for a in alphas:
                for b in betas:
                    for g in gammas:
                        sconf = SolverConfig("Test alpha={}, beta={}, gamma={}".format(str(a), str(b), str(g)))
                        sconf.alpha = a
                        sconf.beta = b
                        sconf.gamma = g
                        solver_configs.append(sconf)

            # sconf = SolverConfig("Test")
            # solver_configs.append(sconf)

            for solver_config in solver_configs:
                solvers.append(Solver(config, solver_config))

            for solver in solvers:
                solver.solve()

            best_solution = max(solvers, key=attrgetter('score'))
            print("Best solver: {} ({})".format(best_solution.solver_config.name, int(best_solution.score)))

            sol = best_solution.solution
            print()
            out_filename = os.path.join(OUTPUTS_PATH, CONF_NAMES[conf_id] + ".out")
            sol.write_result(out_filename)