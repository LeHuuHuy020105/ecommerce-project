import { Button, Rating } from "@mui/material";
import { TfiFullscreen } from "react-icons/tfi";
import { IoMdHeartEmpty } from "react-icons/io";
const ProductItem = () => {
  return (
    <div className="item productItem">
      <div className="imgWrapper">
        <img
          src="https://mediahub.boohoo.com/ydd19578_translucent_xl?qlt=80&w=549&ssz=true&dpr=1"
          className="w-100"
        />
        <span className="badge badge-primary">28%</span>

        <div className="actions">
          <Button><TfiFullscreen/></Button>
          <Button><IoMdHeartEmpty style={{fontSize:'20px'}}/></Button>
        </div>
      </div>
      <div className="info">
        <h4>Item1</h4>
        <span className="text-danger d-block">In stock</span>
        <Rating
          className="mt-2 mb-2"
          name="read-only"
          value={3}
          readOnly
          size="small"
          precision={0.5}
        />
        <div className="d-flex">
          <span className="oldPrice">$200</span>
          <span className="netPrice text-danger ml-2">$100</span>
        </div>
      </div>
    </div>
  );
};
export default ProductItem;
