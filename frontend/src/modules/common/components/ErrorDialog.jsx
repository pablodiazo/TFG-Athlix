import { useIntl, FormattedMessage } from "react-intl";
import PropTypes from "prop-types";
import { NetworkError } from "../../../backend";

const ErrorDialog = ({ error, onClose }) => {
  const intl = useIntl();

  if (!error) return null;

  const message =
    error instanceof NetworkError
      ? intl.formatMessage({ id: "project.global.exceptions.NetworkError" })
      : error.message;

  return (
    <dialog open className="modal">
      <div className="modal-dialog">
        <div className="modal-content">
          <div className="modal-header">
            <h5 className="modal-title">
              <FormattedMessage id="project.common.ErrorDialog.title" />
            </h5>
          </div>
          <div className="modal-body">
            <p>{message}</p>
          </div>
          <div className="modal-footer">
            <button
              type="button"
              className="btn btn-primary"
              onClick={onClose}
            >
              <FormattedMessage id="project.global.buttons.close" />
            </button>
          </div>
        </div>
      </div>
    </dialog>
  );
};

ErrorDialog.propTypes = {
  error: PropTypes.shape({
    message: PropTypes.string,
  }),
  onClose: PropTypes.func.isRequired,
};

export default ErrorDialog;
