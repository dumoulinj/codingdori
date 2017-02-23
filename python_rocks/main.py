import os

from operator import attrgetter


from config import Config
from solver import Solver, SolverConfig

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

        best_solution = max(solvers, key=attrgetter('score'))
        print("Best solver: {} ({})".format(best_solution.solver_config.name, best_solution.score))