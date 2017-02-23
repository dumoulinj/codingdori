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


    def compute_score(self):
        """
        Compute and return the score
        """
        score = 0

        # ...

        return score