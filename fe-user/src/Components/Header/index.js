import { Link } from 'react-router-dom';
import Logo from '../../assests/img/logo.jpg'
import { FiUser } from "react-icons/fi";
import { IoBagOutline } from "react-icons/io5";
import { Button } from '@mui/material';
import SearchBox from '../SearchBox';
import Navigation from '../Navigation';


const Header = () => {
  return (
    <>
      <div className="headerWapper"> 
        <div className="top-strip bg-blue">
            <div className="container">
                <p className="mb-0 mt-0 text-center">Chào mừng bạn đến với <b>EvoMart</b> – Rất hân hạnh được phục vụ bạn!</p>
            </div>
        </div>
        <header className="header">
            <div className="container">
                <div className="row">
                    <div className="logoWrapper d-flex align-items-center col-sm-2">
                        <Link to={'/'}><img src={Logo} alt='Logo'/></Link>
                    </div>
                    <div className='col-sm-10 d-flex align-items-center part-2'>
                      
                      {/* Header Search Start Here */}
                      <SearchBox/>

                       {/* Header Search Start Here */}
                       <div className='part3 d-flex align-items-center ml-auto'>
                          <Button className='circle mr-3'><FiUser/></Button>
                          <div className='ml-auto cartTab d-flex align-items-center'>
                            <span className='price'>$3.29</span>
                            <div className='position-relative ml-2'>
                              <Button className='circle ml-2'><IoBagOutline/></Button>
                              <span className='count d-flex align-items-center justify-content-center'>1</span>
                            </div>
                          </div>
                       </div>
                    </div>
                </div>
            </div>
        </header>

        <Navigation></Navigation>
      </div>
    </>
  );
};
export default Header;
