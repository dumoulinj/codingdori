from operator import itemgetter

import sys


class Cache:
    def __init__(self):
        self.id = -1
        self.videos = []
        self.candidates = []
        self.max_size = 0
        self.available_size = 0
        self.min_candidate_size = sys.maxsize

        self.gains = {}
        self.ordered_gains = []

        self.need_sort = False

    def sort_ordered_gains(self):
        # Order by gain
        self.ordered_gains = [(k, v) for k, v in self.gains.items()]
        self.ordered_gains.sort(key=itemgetter(1), reverse=True)
        self.need_sort = False

    def add_video(self, video_id, video_size):
        """
        Try to add a video to the list. If size available, adds it, update available size and return True. If not,
        return False.
        :param video_id:
        :param video_size:
        :return:
        """
        if self.available_size >= video_size:
            if video_id not in self.videos:
                self.videos.append(video_id)
                self.available_size -= video_size
                return True

        return False

class Endpoint:
    def __init__(self):
        self.latency = 0 # Ld
        self.nb_cache_servers = 0 # K
        self.connections = {} # id: cache_id (c), value: Cache latency (Lc)
        self.requests = []
        self.videos_needed = []

class Request:
    def __init__(self):
        self.requested_video_id = 0 # Rv
        self.endpoint_id = 0 # Re
        self.nb_requests = 0 # Rn

class Config:
    def __init__(self, filename):
        self.filename = filename

        # First section
        self.nb_videos = 0 # V
        self.nb_endpoints = 0 # E
        self.nb_requests = 0 # R
        self.nb_caches = 0 # C
        self.capacity = 0 # X

        # 2nd section
        self.video_sizes = []

        # 3rd section
        self.endpoints = []

        # 4th section
        self.requests = []


        self.caches = []

        self.other_gains = {}

        self.valid = False

    def read_configuration(self):
        """
        Read configuration from input file
        """
        try:
            f = open(self.filename, 'r')

            # First line : V, E, R, C, X
            first_line = f.readline().split(" ")
            self.nb_videos = int(first_line[0])
            self.nb_endpoints = int(first_line[1])
            self.nb_requests = int(first_line[2])
            self.nb_caches = int(first_line[3])
            self.capacity = int(first_line[4])

            # Create caches
            for i in range(self.nb_caches):
                cache = Cache()
                cache.id = i
                cache.available_size = self.capacity
                cache.max_size = self.capacity
                self.caches.append(cache)

            # Second section : videos sizes
            videos_sizes = f.readline().split(" ")
            for video_size in videos_sizes:
                self.video_sizes.append(int(video_size))

            # Third section : endpoints
            for i in range(self.nb_endpoints):
                endpoint_conf = f.readline().split(" ")
                endpoint = Endpoint()
                endpoint.latency = int(endpoint_conf[0])
                endpoint.nb_cache_servers = int(endpoint_conf[1])

                for j in range(endpoint.nb_cache_servers):
                    connection_description = f.readline().split(" ")
                    c = int(connection_description[0])
                    Lc = int(connection_description[1])
                    endpoint.connections[c] = Lc

                self.endpoints.append(endpoint)

            # Last section : request descriptions
            for i in range(self.nb_requests):
                request = Request()
                line = f.readline().split(" ")

                request.requested_video_id = int(line[0])
                request.endpoint_id = int(line[1])
                request.nb_requests = int(line[2])

                self.requests.append(request)
                self.endpoints[request.endpoint_id].requests.append(request)
                self.endpoints[request.endpoint_id].videos_needed.append(request.requested_video_id)

            self.valid = True

            print("Config '{}' read!".format(self.filename))
        except:
            print("Error while reading configuration file : {}".format(self.filename))

    def compute_gains_by_video(self):
        # Loop on endpoints
        for endpoint in self.endpoints:
            # Loop on requests
            for request in endpoint.requests:
                base_latency = (request.nb_requests * endpoint.latency)
                video_id = request.requested_video_id

                # Loop on connected cache servers, and compute gain
                cache_gains = []
                for cache_id, latency in endpoint.connections.items():
                    # Compute latency gain for the video
                    gain = base_latency - (request.nb_requests * latency)
                    cache_gains.append((cache_id, gain))

                # Select best cache and update it
                if len(cache_gains):
                    cache_gains.sort(key=itemgetter(1), reverse=True)
                    cache_id = cache_gains[0][0]
                    cache_gain = cache_gains[0][1]
                    cache = self.caches[cache_id]
                    if video_id in cache.gains:
                        cache.gains[video_id] += cache_gain
                    else:
                        cache.gains[video_id] = cache_gain

                    # Update min candidate size
                    video_size = self.video_sizes[video_id]
                    cache.min_candidate_size = min(cache.min_candidate_size, video_size)

                    # In prevision to add to other caches if best is full
                    key = (video_id, cache_id)
                    value = cache_gains[1:]
                    self.other_gains[key] = value

        # Loop on servers
        for cache in self.caches:
            cache.sort_ordered_gains()
            #cache.gains = {}
