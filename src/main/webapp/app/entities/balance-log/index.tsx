import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import BalanceLog from './balance-log';
import BalanceLogDetail from './balance-log-detail';
import BalanceLogUpdate from './balance-log-update';
import BalanceLogDeleteDialog from './balance-log-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={BalanceLogUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={BalanceLogUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={BalanceLogDetail} />
      <ErrorBoundaryRoute path={match.url} component={BalanceLog} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={BalanceLogDeleteDialog} />
  </>
);

export default Routes;
