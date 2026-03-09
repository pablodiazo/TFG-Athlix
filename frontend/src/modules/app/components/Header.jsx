import { useSelector } from "react-redux";
import { Link } from "react-router-dom";
import users, { UserMenu } from "../../users";


import "../css/Header.css";

const Header = () => {
  const userName = useSelector(users.selectors.getUserName);
  const isLoggedIn = useSelector(users.selectors.isLoggedIn);
  const userRole = useSelector(users.selectors.getRole);
  
  return (
    <header className="athlix-header">
      <div className="athlix-header-container">
        <div className="athlix-logo-wrapper">
          {isLoggedIn ? (
            <>
            {userRole === "USER" && (
              <Link className="athlix-logo-link" to="/users/profile">
                <h1 className="athlix-logo-text">
                  ATHLIX<span className="athlix-accent">.</span>
                </h1>
              </Link>
            )}
            {userRole === "COACH" && (
              <Link className="athlix-logo-link" to="/users/profile">
                <h1 className="athlix-logo-text">
                  ATHLIX<span className="athlix-accent">.</span>
                </h1>
              </Link>
            )}
            </>
          ): (
            <Link className="athlix-logo-link" to="/">
              <h1 className="athlix-logo-text">
                ATHLIX<span className="athlix-accent">.</span>
              </h1>
            </Link>)
          }
        </div>
        <nav className="athlix-main-nav">
          {/*Enlaces principales*/}
        </nav>
        <div className="athlix-header-actions">
          {userName && <UserMenu />}
        </div>
      </div>
    </header>
  );
};

export default Header;
