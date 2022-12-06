import unittest
from model import *
from weighted_budget_maxcover import weighted_budget_maximum_cover


class TestSum(unittest.TestCase):
    def test_budget_cover(self):
        ## ARRANGE
        #Universe, set
        ioc0 = IOC("jhdkgh@gmail.com", 5)
        ioc1 = IOC("Ip:128.2.2.2", 3)
        ioc2 = IOC("www.evil", 14)
        ioc3 = IOC("MD5:98s35956ntnh9659reg286gr35963gf2", 278)
        ioc4 = IOC("IpV6", 17)

        
        #all subsets:
        subset0 = Subset([ioc0, ioc2, ioc1, ioc3], 15)
        subset1 = Subset([ioc2, ioc0], 20)
        subset2 = Subset([ioc4], 20)
        subset3 = Subset([ioc0, ioc2, ioc3], 30)

        subsets = [subset0, subset1, subset2, subset3]
        budget = 85
        ## ACT    
        cover = weighted_budget_maximum_cover(budget, subsets) 

        ## ASSERT
        print(subset2)
        self.assertEqual(cover, [subset0, subset2])


    def test_budget_cover_one(self):
        ## ARRANGE
        #Universe, set
        ioc0 = IOC("jhdkgh@gmail.com", 5)
        ioc1 = IOC("Ip:128.2.2.2", 3)
        ioc2 = IOC("www.evil", 14)
        ioc3 = IOC("MD5:98s35956ntnh9659reg286gr35963gf2", 278)
        ioc4 = IOC("IpV6", 17)

        
        #all subsets:
        subset0 = Subset([ioc0, ioc2, ioc1, ioc3], 15)
        subset1 = Subset([ioc2, ioc0], 20)
        subset2 = Subset([ioc4], 20)
        subset3 = Subset([ioc0, ioc2, ioc3], 30)

        subsets = [subset0, subset1, subset2, subset3]
        budget = 20
        ## ACT    
        cover = weighted_budget_maximum_cover(budget, subsets) 

        ## ASSERT
        print(subset2)
        self.assertEqual(cover, [subset0])


    def test_budget_cover_zero(self):
        ## ARRANGE
        #Universe, set
        ioc0 = IOC("jhdkgh@gmail.com", 5)
        ioc1 = IOC("Ip:128.2.2.2", 3)
        ioc2 = IOC("www.evil", 14)
        ioc3 = IOC("MD5:98s35956ntnh9659reg286gr35963gf2", 278)
        ioc4 = IOC("IpV6", 17)

        
        #all subsets:
        subset0 = Subset([ioc0, ioc2, ioc1, ioc3], 15)
        subset1 = Subset([ioc2, ioc0], 20)
        subset2 = Subset([ioc4], 20)
        subset3 = Subset([ioc0, ioc2, ioc3], 30)

        subsets = [subset0, subset1, subset2, subset3]
        budget = 0
        ## ACT    
        cover = weighted_budget_maximum_cover(budget, subsets) 

        ## ASSERT
        print(subset2)
        self.assertEqual(cover, [])    
if __name__ == '__main__':
    unittest.main()