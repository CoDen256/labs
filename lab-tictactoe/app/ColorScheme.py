class Color:
    def __init__(self):
        self.color_scheme = 0
        self.max_colors = 2

    @property
    def background_mainmenu(self):
        return [(100, 150, 175),
                (221, 116, 252)][self.color_scheme]

    @property
    def background_game(self):
        return [(220, 220, 220),
                (220, 220, 220)][self.color_scheme]

    @property
    def background_selection(self):
        return [(100, 150, 175),
                (221, 116, 252)][self.color_scheme]

    @property
    def cross(self):
        return [(255, 50, 50),
                (224, 74, 144)][self.color_scheme]

    @property
    def circle(self):
        return [(50, 50, 255),
                (134, 63, 181)][self.color_scheme]

    @property
    def grid(self):
        return [(50, 50, 50),
                (50, 50, 50)][self.color_scheme]

    @property
    def grid_box(self):
        return [(250, 0, 0),
                (163, 39, 245)][self.color_scheme]

    @property
    def cell(self):
        return [(0, 0, 0),
                (0, 0, 0)][self.color_scheme]

    @property
    def cell_hov(self):
        return [(180, 180, 180),
                (220, 120, 245)][self.color_scheme]

    @property
    def text(self):
        return [(50, 50, 50),
                (50, 50, 50)][self.color_scheme]

    @property
    def thinking_text(self):
        return [(100, 100, 100),
                (100, 100, 100)][self.color_scheme]

    @property
    def victory_text(self):
        return [(250, 100, 100),
                (250, 100, 100)][self.color_scheme]

    @property
    def button(self):
        return [(230, 130, 100),
                (204, 45, 138)][self.color_scheme]

    @property
    def but_text(self):
        return [(25, 25, 25),
                (25, 25, 25)][self.color_scheme]

    @property
    def but_hovered(self):
        return [(250, 250, 250),
                (250, 250, 250)][self.color_scheme]

    @property
    def but_released(self):
        return [(50, 50, 50),
                (50, 50, 50)][self.color_scheme]
