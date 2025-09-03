import React from "react";
import Slider from "react-slick";

const HomeBanner = () => {
  var settings = {
    dots: false, // Hiện chấm tròn (pagination) bên dưới => false = không hiện
    infinite: true, // Vòng lặp vô hạn, chạy hết quay lại ảnh đầu
    speed: 500, // Thời gian chuyển slide (ms), ở đây là 0.5 giây
    slidesToShow: 1, // Số slide hiển thị trên màn hình cùng lúc (ở đây là 1 ảnh)
    slidesToScroll: 1, // Khi chuyển slide, cuộn mấy ảnh một lần (ở đây là 1 ảnh)
    arrows: true, // Hiện mũi tên điều hướng trái/phải
    autoplay: true, // bật chế độ tự động
    autoplaySpeed: 3000, // 3 giây tự động chuyển ảnh
  };

  return (
    <div className="homeBannerSection">
      <Slider {...settings}>
        <div className="item">
          <img
            src="https://mediahub.debenhams.com/dbz_prod_260825_DESK_25OFFREVOLUTION_UK?qlt=80&w=1920&h=822&ttl=86400&dpr=1&fit=cvr"
            className="w-100"
          />
        </div>
        <div className="item">
          <img
            src="https://mediahub.debenhams.com/dbz_prod_210825_DESK_SAFFIELAUNCH_ROTATE_PP_UK?qlt=80&w=1920&h=822&ttl=86400&dpr=1&fit=cvr"
            className="w-100"
          />
        </div>
        <div className="item">
          <img
            src="https://mediahub.debenhams.com/dbz_prod_0808_WEAREBASIC_SPLASH_DSK?qlt=80&w=1920&h=822&ttl=86400&dpr=1&fit=cvr"
            className="w-100"
          />
        </div>
        <div className="item">
          <img
            src="https://mediahub.debenhams.com/dbz_prod_070825_DESK_DSGNSPLASH_UK?qlt=80&w=1920&h=822&ttl=86400&dpr=1&fit=cvr"
            className="w-100"
          />
        </div>
      </Slider>
    </div>
  );
};
export default HomeBanner;
