import unittest

from weighted_max_cover import *


class TestSum(unittest.TestCase):
   def test_set_cover(self):
        #Universe, set
        ioc0 = IOC("jhdkgh@gmail.com", 2)
        ioc1 = IOC("Ip:128.2.2.2", 36)
        ioc2 = IOC("www.evil", 34)
        ioc3 = IOC("MD5:98s35956ntnh9659reg286gr35963gf2", 128)
        ioc4 = IOC("IpV6lkrjj", 15)
        ioc7 = IOC("some_info", 10)

        k = 1

        #all subsets:
        subset3 = Subset([ioc0, ioc2, ioc1, ioc3, ioc3], 1)
        subset1 = Subset([ioc2, ioc0, ioc4], 1)
        subset2 = Subset([ioc4, ioc7, ioc1], 1)
        subset0 = Subset([ioc0, ioc2], 1)

        subsets = [subset0, subset1, subset2, subset3]

        ## ACT    
        cover = weighted_maximum_cover(k, subsets) 

        ## ASSERT
        self.assertEqual(cover, [subset3])

   def test_set_cover_equal_weight_ioc(self):
        #Universe, set
        ioc0 = IOC("jhdkgh@gmail.com", 2)
        ioc1 = IOC("Ip:128.2.2.2", 2)

        ## ACT    
        target = Subset([ioc0], 0)
        subset = heaviest_subset([target, Subset([ioc1], 0)], set())

        ## ASSERT
        self.assertEqual(target, subset)

   def test_set_cover_one(self):
        k1 = 2

        ioc0 = IOC("jhdkgh@gmail.com", 2)
        ioc1 = IOC("Ip:128.2.2.2", 36)
        ioc2 = IOC("www.evil", 34)
        ioc3 = IOC("MD5:98s35956ntnh9659reg286gr35963gf2", 128)
        ioc4 = IOC("IpV6lkrjj", 15)
        ioc5 = IOC("IpV4:189.91.4.11", 3)
        ioc6 = IOC("www.killme.plz", 48)
        ioc7 = IOC("some_info", 10)

        subset0 = Subset([ioc0, ioc2, ioc1, ioc3, ioc5], 1)
        subset1 = Subset([ioc0, ioc2, ioc1, ioc3, ioc6], 1)
        subset2 = Subset([ioc0], 1)
        subset3 = Subset([ioc0, ioc2], 1)


        subsets = [subset0, subset1, subset2, subset3]

       ## ACT 
        cover1 = weighted_maximum_cover(k1, subsets) 

        ## ASSERT
        print(cover1)
        print("end")
        self.assertEqual(cover1,[subset1,subset0])

   def test_set_cover_two(self):
        k1 = 8

        ioc0 = IOC("jhdkgh@gmail.com", 2)
        ioc1 = IOC("Ip:128.2.2.2", 36)
        ioc2 = IOC("www.evil", 34)
        ioc3 = IOC("MD5:98s35956ntnh9659reg286gr35963gf2", 128)
        ioc4 = IOC("IpV6lkrjj", 15)
        ioc5 = IOC("IpV4:189.91.4.11", 3)
        ioc6 = IOC("www.killme.plz", 48)
        ioc7 = IOC("some_info", 10)

        subset0 = Subset([ioc0, ioc3], 1)
        subset1 = Subset([ioc1, ioc3, ioc6], 1)
        subset2 = Subset([ioc0], 1)
        subset3 = Subset([ioc0, ioc2], 1)
        subset4 = Subset([ioc7, ioc2, ioc4, ioc0], 1)
        subset5 = Subset([ioc0, ioc2], 1)
        subset6 = Subset([ioc5], 1)

        subsets = [subset0, subset1, subset2, subset3, subset4, subset5, subset6]

       ## ACT 
        cover1 = weighted_maximum_cover(k1, subsets) 

        ## ASSERT
        print(cover1)
        self.assertEqual(cover1, [subset1, subset4, subset6])

if __name__ == '__main__':
    unittest.main()