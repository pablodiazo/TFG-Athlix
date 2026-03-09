import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import {IntlProvider} from 'react-intl';

import './index.css';
import registerServiceWorker from './registerServiceWorker';

import { App } from "./modules/app";
import store from "./store"
import { initReactIntl } from './i18n';

import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap';

/* Configure i18n. */
const { locale, messages } = initReactIntl();

ReactDOM.render(
  <Provider store={store}>
    <IntlProvider locale={locale} messages={messages}>
      <App />
    </IntlProvider>
  </Provider>,
  document.getElementById('root')
);
registerServiceWorker();
