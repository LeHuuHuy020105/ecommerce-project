import { Button } from "@mui/material";
import HomeBanner from "../../Components/HomeBanner";
import { IoIosArrowRoundForward } from "react-icons/io";
import { Swiper, SwiperSlide } from 'swiper/react';
import { Pagination } from 'swiper/modules';
import 'swiper/css';
import 'swiper/css/pagination';
import ProductItem from "../../Components/ProductItem";

const Home = () => {
  return (
    <>
      <HomeBanner />
      <section className="homeProducts">
        <div className="container">
          <div className="row">
            <div className="col-md-3">
              <div className="banner">
                <img
                  src="https://mediahub.boohoo.com/ydd19578_translucent_xl?qlt=80&w=549&ssz=true&dpr=1"
                  className="cursor"
                ></img>
              </div>

              <div className="banner mt-3">
                <img
                  src="https://mediahub.boohoo.com/ydd19578_translucent_xl?qlt=80&w=549&ssz=true&dpr=1"
                  className="cursor"
                ></img>
              </div>
            </div>
            <div className="col-md-9 productRow">
              <div className="d-flex align-items-center mt-4">
                <div className="info w-75">
                  <h3 className="mb-0 hd">BEST SELLER</h3>
                  <p className="text-light text-sml mb-0">
                    Do not miss the current offers until the end of March.
                  </p>
                </div>
                <Button className="viewAllBtn ml-auto">
                  View All <IoIosArrowRoundForward />
                </Button>
              </div>

              <div className="product_row w-100 mt-4">
                <Swiper
                  slidesPerView={3}
                  spaceBetween={0}
                  pagination={{
                    clickable: true,
                  }}
                  modules={[Pagination]}
                  className="mySwiper"
                >
                  <SwiperSlide>
                    <ProductItem/>
                  </SwiperSlide>

                  <SwiperSlide>
                    <ProductItem/>
                  </SwiperSlide>

                  <SwiperSlide>
                    <ProductItem/>
                  </SwiperSlide>

                  <SwiperSlide>
                    <ProductItem/>
                  </SwiperSlide>

                  <SwiperSlide>
                    <ProductItem/>
                  </SwiperSlide>

                </Swiper>
              </div>

              <div className="d-flex align-items-center mt-4">
                <div className="info w-75">
                  <h3 className="mb-0 hd">BEST SELLER</h3>
                  <p className="text-light text-sml mb-0">
                    Do not miss the current offers until the end of March.
                  </p>
                </div>
                <Button className="viewAllBtn ml-auto">
                  View All <IoIosArrowRoundForward />
                </Button>
              </div>

              <div className="product_row w-100 mt-4">
                <Swiper
                  slidesPerView={3}
                  spaceBetween={0}
                  pagination={{
                    clickable: true,
                  }}
                  modules={[Pagination]}
                  className="mySwiper"
                >
                  <SwiperSlide>
                    <ProductItem/>
                  </SwiperSlide>

                  <SwiperSlide>
                    <ProductItem/>
                  </SwiperSlide>

                  <SwiperSlide>
                    <ProductItem/>
                  </SwiperSlide>

                  <SwiperSlide>
                    <ProductItem/>
                  </SwiperSlide>

                  <SwiperSlide>
                    <ProductItem/>
                  </SwiperSlide>

                </Swiper>
              </div>
            </div>
          </div>
        </div>
      </section>
    </>
  );
};
export default Home;
