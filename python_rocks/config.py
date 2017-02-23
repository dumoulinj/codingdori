class Config:
    def __init__(self, filename):
        self.filename = filename
        self.row = 0
        self.col = 0
        self.l = 0
        self.h = 0

        self.pizza = []

        self.valid = False

        self.read_configuration()

    def read_configuration(self):
        """
        Read configuration from input file
        """
        try:
            f = open(self.filename, 'r')

            conf = f.readline().split(" ")
            self.row = int(conf[0])
            self.col = int(conf[1])
            self.l = int(conf[2])
            self.h = int(conf[3])

            for r in range(self.row):
                line = f.readline()
                self.pizza.append(list(line))

            self.valid = True
        except:
            print("Error while reading configuration file : {}".format(self.filename))