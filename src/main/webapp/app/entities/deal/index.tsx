import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Deal from './deal';
import DealDetail from './deal-detail';
import DealUpdate from './deal-update';
import DealDeleteDialog from './deal-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={DealUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={DealUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={DealDetail} />
      <ErrorBoundaryRoute path={match.url} component={Deal} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={DealDeleteDialog} />
  </>
);

export default Routes;
