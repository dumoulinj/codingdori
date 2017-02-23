from operator import attrgetter

from solution import Solution


class SolverConfig:
    """
    Configuration for one solver
    """
    def __init__(self, name):
        self.name = name


class Solver:
    """
    One solver instance
    """
    def __init__(self, config, solver_config):
        self.config = config
        self.solver_config = solver_config

        self.solutions = []

        self.best_solution = None
        self.score = 0


    def find_best_solution(self):
        """
        Find the solution with the highest score
        """
        self.best_solution = max(self.solutions, key=attrgetter('score'))
        self.score = self.best_solution.score


    def solve(self):
        print("Solver started: {}".format(self.solver_config.name))

        sol_a = Solution()
        sol_a.score = 10

        sol_b = Solution()
        sol_b.score = 22

        self.solutions.append(sol_a)
        self.solutions.append(sol_b)

        self.find_best_solution()
