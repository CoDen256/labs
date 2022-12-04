import unittest
from model import *
from set_cover import set_cover


class TestSum(unittest.TestCase):
    def test_set_cover(self):
        ## ARRANGE
        #Universe, set
        ioc0 = IOC("jhdkgh@gmail.com", 2)
        ioc1 = IOC("Ip:128.2.2.2", 36)
        ioc2 = IOC("www.evil", 34)
        ioc3 = IOC("MD5:98s35956ntnh9659reg286gr35963gf2", 278)
        ioc4 = IOC("IpV6", 13)
        universe = {ioc0.name, ioc1.name, ioc2.name, ioc3.name, ioc4.name}

        
        #all subsets:
        subset0 = Subset([ioc0, ioc2, ioc1, ioc3], 1)
        subset1 = Subset([ioc2, ioc0], 1)
        subset2 = Subset([ioc4], 1)
        subset3 = Subset([ioc0, ioc2], 1)

        subset0 = set(subset0.iocs_list)
        subset1 = set(subset1.iocs_list)
        subset2 = set(subset2.iocs_list)
        subset3 = set(subset3.iocs_list)
        subsets = [subset0, subset1, subset2, subset3]

        ## ACT    
        cover = set_cover(universe, subsets) 

        ## ASSERT
        self.assertEqual(cover, [{'Ip:128.2.2.2', 'jhdkgh@gmail.com', 'www.evil', 'MD5:98s35956ntnh9659reg286gr35963gf2'}, {'IpV6'}])

    def test_set_cover_one(self):
        ## ARRANGE
        #Universe, set
        ioc0 = IOC("jhdkgh@gmail.com", 2)
        universe = {ioc0.name}

        
        #all subsets:
        subset0 = Subset([ioc0], 1)
        subset0 = set(subset0.iocs_list)
        subsets = [subset0]

        ## ACT    
        cover = set_cover(universe, subsets) 

        ## ASSERT
        self.assertEqual(cover, [{"jhdkgh@gmail.com"}])
    def test_subset(self):
        ## ARRANGE
        ioc0 = IOC("jhdkgh@gmail.com", 2)
        ioc1 = IOC("Ip:128.2.2.2", 36)
        

        ## ACT 
        s = Subset([ioc0, ioc1], 0)   
        result = s.iocs_list

        i0 = s.get_IOC_by_index(0)

        ## ASSERT
        self.assertEqual(["jhdkgh@gmail.com", "Ip:128.2.2.2"], result )
        self.assertEqual("jhdkgh@gmail.com", i0)
    
#  print(ioc0.__dict__)     !!!!!!!!!!!!!

if __name__ == '__main__':
    unittest.main()