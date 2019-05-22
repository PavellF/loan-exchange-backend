import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { IDeal } from 'app/shared/model/deal.model';
import { getEntities as getDeals } from 'app/entities/deal/deal.reducer';
import { getEntity, updateEntity, createEntity, reset } from './notification.reducer';
import { INotification } from 'app/shared/model/notification.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface INotificationUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface INotificationUpdateState {
  isNew: boolean;
  recipientId: string;
  associatedDealId: string;
}

export class NotificationUpdate extends React.Component<INotificationUpdateProps, INotificationUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      recipientId: '0',
      associatedDealId: '0',
      isNew: !this.props.match.params || !this.props.match.params.id
    };
  }

  componentWillUpdate(nextProps, nextState) {
    if (nextProps.updateSuccess !== this.props.updateSuccess && nextProps.updateSuccess) {
      this.handleClose();
    }
  }

  componentDidMount() {
    if (!this.state.isNew) {
      this.props.getEntity(this.props.match.params.id);
    }

    this.props.getUsers();
    this.props.getDeals();
  }

  saveEntity = (event, errors, values) => {
    values.date = convertDateTimeToServer(values.date);

    if (errors.length === 0) {
      const { notificationEntity } = this.props;
      const entity = {
        ...notificationEntity,
        ...values
      };

      if (this.state.isNew) {
        this.props.createEntity(entity);
      } else {
        this.props.updateEntity(entity);
      }
    }
  };

  handleClose = () => {
    this.props.history.push('/entity/notification');
  };

  render() {
    const { notificationEntity, users, deals, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="loanExchangeBackendApp.notification.home.createOrEditLabel">
              <Translate contentKey="loanExchangeBackendApp.notification.home.createOrEditLabel">Create or edit a Notification</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : notificationEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="notification-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="notification-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="dateLabel" for="notification-date">
                    <Translate contentKey="loanExchangeBackendApp.notification.date">Date</Translate>
                  </Label>
                  <AvInput
                    id="notification-date"
                    type="datetime-local"
                    className="form-control"
                    name="date"
                    placeholder={'YYYY-MM-DD HH:mm'}
                    value={isNew ? null : convertDateTimeFromServer(this.props.notificationEntity.date)}
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="typeLabel" for="notification-type">
                    <Translate contentKey="loanExchangeBackendApp.notification.type">Type</Translate>
                  </Label>
                  <AvInput
                    id="notification-type"
                    type="select"
                    className="form-control"
                    name="type"
                    value={(!isNew && notificationEntity.type) || 'NEW_DEAL_OPEN'}
                  >
                    <option value="NEW_DEAL_OPEN">
                      <Translate contentKey="loanExchangeBackendApp.BalanceLogEvent.NEW_DEAL_OPEN" />
                    </option>
                    <option value="LOAN_TAKEN">
                      <Translate contentKey="loanExchangeBackendApp.BalanceLogEvent.LOAN_TAKEN" />
                    </option>
                    <option value="PERCENT_CHARGE">
                      <Translate contentKey="loanExchangeBackendApp.BalanceLogEvent.PERCENT_CHARGE" />
                    </option>
                    <option value="DEAL_PAYMENT">
                      <Translate contentKey="loanExchangeBackendApp.BalanceLogEvent.DEAL_PAYMENT" />
                    </option>
                    <option value="DEAL_CLOSED">
                      <Translate contentKey="loanExchangeBackendApp.BalanceLogEvent.DEAL_CLOSED" />
                    </option>
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="notification-recipient">
                    <Translate contentKey="loanExchangeBackendApp.notification.recipient">Recipient</Translate>
                  </Label>
                  <AvInput id="notification-recipient" type="select" className="form-control" name="recipient.id">
                    <option value="" key="0" />
                    {users
                      ? users.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="notification-associatedDeal">
                    <Translate contentKey="loanExchangeBackendApp.notification.associatedDeal">Associated Deal</Translate>
                  </Label>
                  <AvInput id="notification-associatedDeal" type="select" className="form-control" name="associatedDeal.id">
                    <option value="" key="0" />
                    {deals
                      ? deals.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/notification" replace color="info">
                  <FontAwesomeIcon icon="arrow-left" />
                  &nbsp;
                  <span className="d-none d-md-inline">
                    <Translate contentKey="entity.action.back">Back</Translate>
                  </span>
                </Button>
                &nbsp;
                <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                  <FontAwesomeIcon icon="save" />
                  &nbsp;
                  <Translate contentKey="entity.action.save">Save</Translate>
                </Button>
              </AvForm>
            )}
          </Col>
        </Row>
      </div>
    );
  }
}

const mapStateToProps = (storeState: IRootState) => ({
  users: storeState.userManagement.users,
  deals: storeState.deal.entities,
  notificationEntity: storeState.notification.entity,
  loading: storeState.notification.loading,
  updating: storeState.notification.updating,
  updateSuccess: storeState.notification.updateSuccess
});

const mapDispatchToProps = {
  getUsers,
  getDeals,
  getEntity,
  updateEntity,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(NotificationUpdate);
