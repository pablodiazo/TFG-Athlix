import PropTypes from "prop-types";

const Success = ({ message, onClose }) =>
  message && (
    <div
      className="alert alert-success alert-dismissible fade show"
      role="alert"
    >
      {message}
      <button
        type="button"
        className="btn-close"
        data-bs-dismiss="alert"
        aria-label="Close"
        onClick={onClose}
      ></button>
    </div>
  );

Success.propTypes = {
  message: PropTypes.string,         // Mensaje opcional
  onClose: PropTypes.func.isRequired // onClose obligatorio
};

export default Success;
