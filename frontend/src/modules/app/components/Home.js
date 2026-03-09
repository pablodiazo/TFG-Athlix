import { useNavigate } from "react-router-dom";
import "../css/Home.css";

const Home = () => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate("/users/login");
  };

  return (
    <div className="athlix-home-wrapper">
      <div className="athlix-hero-content">
        <h1 className="athlix-hero-title" data-testid="NewHome_2">
          PLANIFICA TU <span className="athlix-accent">RENDIMIENTO</span>
        </h1>
        <p className="athlix-hero-subtitle">
          Una plataforma para la planificación y monitorización de atletas.
        </p>
        <button 
          className="athlix-btn athlix-btn-primary athlix-hero-btn" 
          data-testid="NewHome_3" 
          onClick={handleClick}
        >
          Acceder
        </button>
      </div>
    </div>
  );
};

export default Home;