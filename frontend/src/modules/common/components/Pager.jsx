import { FormattedMessage } from "react-intl";
import PropTypes from "prop-types";

const Pager = ({ back, next }) => (
  <nav aria-label="page navigation">
    <ul className="pagination justify-content-center">
      <li className={`page-item ${back.enabled ? "" : "disabled"}`}>
        <button className="page-link" onClick={back.onClick} disabled={!back.enabled}>
          <FormattedMessage id="project.global.buttons.back" />
        </button>
      </li>
      <li className={`page-item ${next.enabled ? "" : "disabled"}`}>
        <button className="page-link" onClick={next.onClick} disabled={!next.enabled}>
          <FormattedMessage id="project.global.buttons.next" />
        </button>
      </li>
    </ul>
  </nav>
);

Pager.propTypes = {
  back: PropTypes.shape({
    enabled: PropTypes.bool.isRequired,
    onClick: PropTypes.func.isRequired,
  }).isRequired,
  next: PropTypes.shape({
    enabled: PropTypes.bool.isRequired,
    onClick: PropTypes.func.isRequired,
  }).isRequired,
};

export default Pager;
