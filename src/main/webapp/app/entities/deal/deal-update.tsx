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
import { getEntity, updateEntity, createEntity, reset } from './deal.reducer';
import { IDeal } from 'app/shared/model/deal.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IDealUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IDealUpdateState {
  isNew: boolean;
  emitterId: string;
  recipientId: string;
}

export class DealUpdate extends React.Component<IDealUpdateProps, IDealUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      emitterId: '0',
      recipientId: '0',
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
  }

  saveEntity = (event, errors, values) => {
    values.dateOpen = convertDateTimeToServer(values.dateOpen);
    values.dateBecomeActive = convertDateTimeToServer(values.dateBecomeActive);
    values.endDate = convertDateTimeToServer(values.endDate);

    if (errors.length === 0) {
      const { dealEntity } = this.props;
      const entity = {
        ...dealEntity,
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
    this.props.history.push('/entity/deal');
  };

  render() {
    const { dealEntity, users, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="loanExchangeBackendApp.deal.home.createOrEditLabel">
              <Translate contentKey="loanExchangeBackendApp.deal.home.createOrEditLabel">Create or edit a Deal</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : dealEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="deal-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="deal-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="dateOpenLabel" for="deal-dateOpen">
                    <Translate contentKey="loanExchangeBackendApp.deal.dateOpen">Date Open</Translate>
                  </Label>
                  <AvInput
                    id="deal-dateOpen"
                    type="datetime-local"
                    className="form-control"
                    name="dateOpen"
                    placeholder={'YYYY-MM-DD HH:mm'}
                    value={isNew ? null : convertDateTimeFromServer(this.props.dealEntity.dateOpen)}
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="dateBecomeActiveLabel" for="deal-dateBecomeActive">
                    <Translate contentKey="loanExchangeBackendApp.deal.dateBecomeActive">Date Become Active</Translate>
                  </Label>
                  <AvInput
                    id="deal-dateBecomeActive"
                    type="datetime-local"
                    className="form-control"
                    name="dateBecomeActive"
                    placeholder={'YYYY-MM-DD HH:mm'}
                    value={isNew ? null : convertDateTimeFromServer(this.props.dealEntity.dateBecomeActive)}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="endDateLabel" for="deal-endDate">
                    <Translate contentKey="loanExchangeBackendApp.deal.endDate">End Date</Translate>
                  </Label>
                  <AvInput
                    id="deal-endDate"
                    type="datetime-local"
                    className="form-control"
                    name="endDate"
                    placeholder={'YYYY-MM-DD HH:mm'}
                    value={isNew ? null : convertDateTimeFromServer(this.props.dealEntity.endDate)}
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="startBalanceLabel" for="deal-startBalance">
                    <Translate contentKey="loanExchangeBackendApp.deal.startBalance">Start Balance</Translate>
                  </Label>
                  <AvField
                    id="deal-startBalance"
                    type="text"
                    name="startBalance"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      min: { value: 0, errorMessage: translate('entity.validation.min', { min: 0 }) },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="percentLabel" for="deal-percent">
                    <Translate contentKey="loanExchangeBackendApp.deal.percent">Percent</Translate>
                  </Label>
                  <AvField
                    id="deal-percent"
                    type="text"
                    name="percent"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      min: { value: 0, errorMessage: translate('entity.validation.min', { min: 0 }) },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="successRateLabel" for="deal-successRate">
                    <Translate contentKey="loanExchangeBackendApp.deal.successRate">Success Rate</Translate>
                  </Label>
                  <AvField
                    id="deal-successRate"
                    type="string"
                    className="form-control"
                    name="successRate"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="termLabel" for="deal-term">
                    <Translate contentKey="loanExchangeBackendApp.deal.term">Term</Translate>
                  </Label>
                  <AvField
                    id="deal-term"
                    type="string"
                    className="form-control"
                    name="term"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      min: { value: 0, errorMessage: translate('entity.validation.min', { min: 0 }) },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="paymentEveryLabel" for="deal-paymentEvery">
                    <Translate contentKey="loanExchangeBackendApp.deal.paymentEvery">Payment Every</Translate>
                  </Label>
                  <AvInput
                    id="deal-paymentEvery"
                    type="select"
                    className="form-control"
                    name="paymentEvery"
                    value={(!isNew && dealEntity.paymentEvery) || 'DAY'}
                  >
                    <option value="DAY">
                      <Translate contentKey="loanExchangeBackendApp.PaymentInterval.DAY" />
                    </option>
                    <option value="MONTH">
                      <Translate contentKey="loanExchangeBackendApp.PaymentInterval.MONTH" />
                    </option>
                    <option value="ONE_TIME">
                      <Translate contentKey="loanExchangeBackendApp.PaymentInterval.ONE_TIME" />
                    </option>
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label id="statusLabel" for="deal-status">
                    <Translate contentKey="loanExchangeBackendApp.deal.status">Status</Translate>
                  </Label>
                  <AvInput
                    id="deal-status"
                    type="select"
                    className="form-control"
                    name="status"
                    value={(!isNew && dealEntity.status) || 'PENDING'}
                  >
                    <option value="PENDING">
                      <Translate contentKey="loanExchangeBackendApp.DealStatus.PENDING" />
                    </option>
                    <option value="ACTIVE">
                      <Translate contentKey="loanExchangeBackendApp.DealStatus.ACTIVE" />
                    </option>
                    <option value="CLOSED">
                      <Translate contentKey="loanExchangeBackendApp.DealStatus.CLOSED" />
                    </option>
                    <option value="SUCCESS">
                      <Translate contentKey="loanExchangeBackendApp.DealStatus.SUCCESS" />
                    </option>
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="deal-emitter">
                    <Translate contentKey="loanExchangeBackendApp.deal.emitter">Emitter</Translate>
                  </Label>
                  <AvInput id="deal-emitter" type="select" className="form-control" name="emitter.id">
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
                  <Label for="deal-recipient">
                    <Translate contentKey="loanExchangeBackendApp.deal.recipient">Recipient</Translate>
                  </Label>
                  <AvInput id="deal-recipient" type="select" className="form-control" name="recipient.id">
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
                <Button tag={Link} id="cancel-save" to="/entity/deal" replace color="info">
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
  dealEntity: storeState.deal.entity,
  loading: storeState.deal.loading,
  updating: storeState.deal.updating,
  updateSuccess: storeState.deal.updateSuccess
});

const mapDispatchToProps = {
  getUsers,
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
)(DealUpdate);
