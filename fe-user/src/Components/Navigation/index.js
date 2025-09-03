import { Button } from "@mui/material";
import { IoIosMenu } from "react-icons/io";
import { FaAngleDown } from "react-icons/fa";
import { Link } from "react-router-dom";
import { useState } from "react";
import { FaAngleRight } from "react-icons/fa6";
const Navigation = () => {
  const [isOpenSidebarVal , setIsOpenSidebarVal] = useState(false);
  return (
    <nav>
      <div className="container">
        <div className="row">
          <div className="col-sm-3 navPart1">
            <div className="catWrapper">
              <Button className="allCartTab align-items-center" onClick={()=>{setIsOpenSidebarVal(!isOpenSidebarVal)}}>
                <span className="icon1 mr-2">
                  <IoIosMenu />
                </span>
                <span className="text">ALL CATEGORIES</span>
                <span className="icon2 ml-2">
                  <FaAngleDown />
                </span>
              </Button>
              <div className={`sidebarNav ${isOpenSidebarVal===true ? 'open': ''}`}>
                <ul>
                  <li><Link to="/"><Button>Home</Button></Link></li>
                  <li>
                    <Link to="/"><Button>Fashion <FaAngleRight className="ml-auto"/></Button></Link>
                    <div className="submenu">
                      <Link to="/"><Button>Clothing</Button></Link>
                      <Link to="/"><Button>Footwear</Button></Link>
                      <Link to="/"><Button>Watches</Button></Link>
                    </div>
                  </li>
                  <li>
                    <Link to="/"><Button>Electronic<FaAngleRight className="ml-auto"/></Button></Link>
                    <div className="submenu">
                      <Link to="/"><Button>Clothing</Button></Link>
                      <Link to="/"><Button>Footwear</Button></Link>
                      <Link to="/"><Button>Watches</Button></Link>
                    </div>
                  </li>
                  <li><Link to="/"><Button>Bakery</Button></Link></li>
                  
                </ul>
              </div>
            </div>
          </div>
          <div className="col-sm-9 navPart2 d-flex align-items-center">
            <ul className="list list-inline ml-alto">
              <li className="list-inline-item">
                <Link to="/">Home</Link>
              </li>
              <li className="list-inline-item">
                <Link to="/">Fashion</Link>
                <div className="submenu shadow">
                  <Link to="/">
                    <Button>Clothing</Button>
                  </Link>
                  <Link to="/">
                    <Button>Footwear</Button>
                  </Link>
                  <Link to="/">
                    <Button>Watches</Button>
                  </Link>
                </div>
              </li>
              <li className="list-inline-item">
                <Link to="/">Electronic</Link>
                <div className="submenu shadow">
                  <Link to="/">
                    <Button>Clothing</Button>
                  </Link>
                  <Link to="/">
                    <Button>Footwear</Button>
                  </Link>
                  <Link to="/">
                    <Button>Watches</Button>
                  </Link>
                </div>
              </li>
              <li className="list-inline-item">
                <Link to="/">Bakery</Link>
                <div className="submenu shadow">
                  <Link to="/">
                    <Button>Clothing</Button>
                  </Link>
                  <Link to="/">
                    <Button>Footwear</Button>
                  </Link>
                  <Link to="/">
                    <Button>Watches</Button>
                  </Link>
                </div>
              </li>
              <li className="list-inline-item">
                <Link to="/">Grocery</Link>
                <div className="submenu shadow">
                  <Link to="/">
                    <Button>Clothing</Button>
                  </Link>
                  <Link to="/">
                    <Button>Footwear</Button>
                  </Link>
                  <Link to="/">
                    <Button>Watches</Button>
                  </Link>
                </div>
              </li>
              <li className="list-inline-item">
                <Link to="/">All</Link>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </nav>
  );
};
export default Navigation;
