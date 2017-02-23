class Endpoint:
    def __init__(self):
        self.latency = 0 # Ld
        self.nb_cache_servers = 0 # K
        self.connections = {} # id: c, value: Lc

class RequestDescription:
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
        self.nb_request_descriptions = 0 # R
        self.nb_caches = 0 # C
        self.capacity = 0 # X

        # 2nd section
        self.video_sizes = []

        # 3rd section
        self.endpoints = []

        # 4th section
        self.request_descriptions = []

        self.valid = False

        self.read_configuration()


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
            self.nb_request_descriptions = int(first_line[2])
            self.nb_caches = int(first_line[3])
            self.capacity = int(first_line[4])

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
            for i in range(self.nb_request_descriptions):
                request_description = RequestDescription()
                line = f.readline().split(" ")

                request_description.requested_video_id = int(line[0])
                request_description.endpoint_id = int(line[1])
                request_description.nb_requests = int(line[2])

                self.request_descriptions.append(request_description)


            self.valid = True
        except:
            print("Error while reading configuration file : {}".format(self.filename))