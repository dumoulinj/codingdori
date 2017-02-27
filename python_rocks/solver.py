import copy
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
        self.gamma = 0.8


class Solver:
    """
    One solver instance
    """
    def __init__(self, config, solver_config):
        self.config = config
        self.solver_config = solver_config
        self.solution = Solution()

        self.score = 0

        self.empty_caches = []


    def solve(self):
        nb_added = 1
        nb_others = min(self.solver_config.alpha, self.config.nb_caches - 1)

        _config = copy.deepcopy(self.config)
        # Take size in account
        for cache in _config.caches:
            for video_id, gain in cache.gains.items():
                a = self.solver_config.beta * (float(gain) / float(_config.max_gain))
                b = self.solver_config.gamma * (float(_config.video_sizes[video_id]) / float(_config.max_size))
                new_gain = a - b
                cache.gains[video_id] = new_gain
            cache.sort_ordered_gains()

        while nb_added:
            nb_added = 0
            # Dumb algo: just pick the video with the most gain and put
            for cache in _config.caches:
                if cache.need_sort:
                    cache.sort_ordered_gains()

                for video_id, gain in cache.ordered_gains[:]:
                    video_size = _config.video_sizes[video_id]
                    if cache.add_video(video_id, video_size):
                        nb_added += 1
                    else:
                        try:
                            other_gains = _config.other_gains[(video_id, cache.id)]
                            for other_gain in other_gains[:nb_others]:
                                other_cache_id = other_gain[0]
                                other_cache_gain = other_gain[1]
                                other_cache = _config.caches[other_cache_id]

                                if other_cache_id not in self.empty_caches:
                                    if other_cache.available_size >= video_size and video_id not in other_cache.videos:
                                        if video_id in other_cache.gains:
                                            other_cache.gains[video_id] += other_cache_gain
                                        else:
                                            other_cache.gains[video_id] = other_cache_gain
                                        other_cache.need_sort = True
                                        # Update min candidate size
                                        other_cache.min_candidate_size = min(other_cache.min_candidate_size, video_size)
                        except:
                            pass

                    # Added, or not, remove it from the list
                    cache.ordered_gains.remove((video_id, gain))
                    del cache.gains[video_id]

                    if cache.available_size < cache.min_candidate_size and cache.id not in self.empty_caches:
                        self.empty_caches.append(cache.id)

            print("new passage, nb added: {}".format(nb_added))

        for cache in _config.caches:
            if len(cache.videos):
                self.solution.cache_servers[cache.id] = cache.videos

        # Get score and print it
        self.score = self.solution.compute_score(_config)
        print("{} (alpha={}, beta={}, gamma={})".format(int(self.score), self.solver_config.alpha, self.solver_config.beta, self.solver_config.gamma))




