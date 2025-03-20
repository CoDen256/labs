import unittest
from model import *
from set_cover import set_cover


class TestSum(unittest.TestCase):
    def test_set_cover(self):
        ## ARRANGE
        #Universe, set
        ioc0 = IOC("jhdkgh@gmail.com", 2)
        ioc1 = IOC("Ip:128.2.2.2",36)
        ioc2 = IOC("www.evil",34)
        ioc3 = IOC("MD5:98s35956ntnh9659reg286gr35963gf2",278)
        ioc4 = IOC("IpV6",13)
        universe = {ioc0, ioc1, ioc2, ioc3, ioc4}

        
        #all subsets:
        subset0 = Subset([ioc0, ioc2, ioc1, ioc3], 1)
        subset1 = Subset([ioc2, ioc0], 1)
        subset2 = Subset([ioc4], 1)
        subset3 = Subset([ioc0, ioc2], 1)

        ## ACT
        cover = set_cover(universe, [subset0, subset1, subset2, subset3])

        ## ASSERT
        self.assertEqual(cover, [
            subset0,
            subset2
        ])

    def test_set_cover_one(self):
        ## ARRANGE
        #Universe, set
        ioc0 = IOC("jhdkgh@gmail.com", 2)
        universe = {ioc0}

        
        #all subsets:
        subset0 = Subset([ioc0], 1)
        ## ACT
        cover = set_cover(universe, [subset0])

        ## ASSERT
        self.assertEqual(cover, [subset0])
    def test_subset(self):
        ## ARRANGE
        ioc0 = IOC("jhdkgh@gmail.com", 2)
        ioc1 = IOC("Ip:128.2.2.2", 36)
        

        ## ACT 
        s = Subset([ioc0, ioc1], 0)   
        result = s.iocs_list

        ## ASSERT
        self.assertEqual([ioc0, ioc1], result)