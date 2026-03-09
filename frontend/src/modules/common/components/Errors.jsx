import { useIntl } from "react-intl";
import PropTypes from "prop-types";

const Errors = ({ errors, onClose }) => {
  const intl = useIntl();

  if (!errors) return null;

  const { globalError, fieldErrors: rawFieldErrors } = errors;

  // Generamos los mensajes de errores de campo
  const fieldErrors = rawFieldErrors?.map((e) => {
    const fieldName = intl.formatMessage({
      id: `project.global.fields.${e.fieldName}`,
    });
    // Usamos fieldName + mensaje como key para evitar usar índices
    return { key: `${e.fieldName}-${e.message}`, message: `${fieldName}: ${e.message}` };
  });

  return (
    <div
      className="alert alert-danger alert-dismissible fade show"
      role="alert"
    >
      {globalError && <p>{globalError}</p>}

      {fieldErrors && fieldErrors.length > 0 && (
        <ul>
          {fieldErrors.map((fieldError) => (
            <li key={fieldError.key}>{fieldError.message}</li>
          ))}
        </ul>
      )}

      <button
        type="button"
        className="btn-close"
        data-bs-dismiss="alert"
        aria-label="Close"
        onClick={onClose}
      ></button>
    </div>
  );
};

// Validación de props
Errors.propTypes = {
  errors: PropTypes.shape({
    globalError: PropTypes.string,
    fieldErrors: PropTypes.arrayOf(
      PropTypes.shape({
        fieldName: PropTypes.string.isRequired,
        message: PropTypes.string.isRequired,
      })
    ),
  }),
  onClose: PropTypes.func.isRequired,
};

export default Errors;
