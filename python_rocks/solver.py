from operator import attrgetter
import numpy as np
from solution import Solution


class SolverConfig:
    """
    Configuration for one solver
    """
    def __init__(self, name):
        self.name = name

        self.alpha = 5
        self.beta = 0.1
        self.gama = 0.8


class Solver:
    """
    One solver instance
    """
    def __init__(self, config, solver_config):
        self.config = config
        self.solver_config = solver_config
        self.solution = Solution()

        self.score = 0

    def solve(self):
        # Dumb algo: just pick the video with the most gain and put
        for cache in self.config.caches:
            for video_id, gain in cache.ordered_gains:
                cache.add_video(video_id, self.config.video_sizes[video_id])
                if cache.available_size < cache.min_candidate_size:
                    break

            if len(cache.videos):
                self.solution.cache_servers[cache.id] = cache.videos

        # Get score and print it
        self.score = self.solution.compute_score(self.config)
        print("{} (alpha={}, beta={}, gama={})".format(int(self.score), self.solver_config.alpha, self.solver_config.beta, self.solver_config.gama))




