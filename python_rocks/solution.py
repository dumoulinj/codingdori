import sys


class Solution:
    def __init__(self):
        # Dict of servers, key: server id, value: list of video ids
        self.cache_servers = dict()

        # self.cache_servers[0] = [2]
        # self.cache_servers[1] = [3, 1]
        # self.cache_servers[2] = [0, 1]

        self.score = 0

    def write_result(self, filename):
        """
        Write result to output file
        """
        f = open(filename, 'w')

        # N : number of cache server descriptions
        f.write(str(len(self.cache_servers)) + "\n")

        for id, videos in self.cache_servers.items():
            if len(videos) > 0:
                f.write(str(id) + " ")
                f.write(" ".join(str(video) for video in videos)+ "\n")


    def compute_score(self, config):
        """
        Compute and return the score
        """
        score = 0
        denom = 0

        try:
            for request_description in config.request_descriptions:
                Re = request_description.endpoint_id
                Rv = request_description.requested_video_id
                endpoint = config.endpoints[Re]
                LD = endpoint.latency
                min_latency = LD

                for c in range(config.nb_caches):
                    if c in self.cache_servers and Rv in self.cache_servers[c] and c in endpoint.connections:
                        latency = endpoint.connections[c]
                        if latency < min_latency:
                            min_latency = latency

                score += ((LD - min_latency) * request_description.nb_requests)
                denom += request_description.nb_requests

            return (score/denom)*1000

        except Exception as e:
            e = sys.exc_info()[0]
            return 0