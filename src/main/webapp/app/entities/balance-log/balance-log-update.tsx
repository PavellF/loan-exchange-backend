import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Label, Row } from 'reactstrap';
import { AvField, AvForm, AvGroup, AvInput } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntities as getDeals } from 'app/entities/deal/deal.reducer';
import { createEntity, getEntity, reset, updateEntity } from './balance-log.reducer';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';

export interface IBalanceLogUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IBalanceLogUpdateState {
  isNew: boolean;
  accountId: string;
  dealId: string;
}

export class BalanceLogUpdate extends React.Component<IBalanceLogUpdateProps, IBalanceLogUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      accountId: '0',
      dealId: '0',
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
      const { balanceLogEntity } = this.props;
      const entity = {
        ...balanceLogEntity,
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
    this.props.history.push('/entity/balance-log');
  };

  render() {
    const { balanceLogEntity, users, deals, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="loanExchangeBackendApp.balanceLog.home.createOrEditLabel">
              <Translate contentKey="loanExchangeBackendApp.balanceLog.home.createOrEditLabel">Create or edit a BalanceLog</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : balanceLogEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="balance-log-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="balance-log-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="dateLabel" for="balance-log-date">
                    <Translate contentKey="loanExchangeBackendApp.balanceLog.date">Date</Translate>
                  </Label>
                  <AvInput
                    id="balance-log-date"
                    type="datetime-local"
                    className="form-control"
                    name="date"
                    placeholder={'YYYY-MM-DD HH:mm'}
                    value={isNew ? null : convertDateTimeFromServer(this.props.balanceLogEntity.date)}
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="oldValueLabel" for="balance-log-oldValue">
                    <Translate contentKey="loanExchangeBackendApp.balanceLog.oldValue">Old Value</Translate>
                  </Label>
                  <AvField
                    id="balance-log-oldValue"
                    type="text"
                    name="oldValue"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="amountChangedLabel" for="balance-log-amountChanged">
                    <Translate contentKey="loanExchangeBackendApp.balanceLog.amountChanged">Amount Changed</Translate>
                  </Label>
                  <AvField
                    id="balance-log-amountChanged"
                    type="text"
                    name="amountChanged"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="typeLabel" for="balance-log-type">
                    <Translate contentKey="loanExchangeBackendApp.balanceLog.type">Type</Translate>
                  </Label>
                  <AvField
                    id="balance-log-type"
                    type="text"
                    name="type"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label for="balance-log-account">
                    <Translate contentKey="loanExchangeBackendApp.balanceLog.account">Account</Translate>
                  </Label>
                  <AvInput id="balance-log-account" type="select" className="form-control" name="account.id">
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
                  <Label for="balance-log-deal">
                    <Translate contentKey="loanExchangeBackendApp.balanceLog.deal">Deal</Translate>
                  </Label>
                  <AvInput id="balance-log-deal" type="select" className="form-control" name="deal.id">
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
                <Button tag={Link} id="cancel-save" to="/entity/balance-log" replace color="info">
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
  balanceLogEntity: storeState.balanceLog.entity,
  loading: storeState.balanceLog.loading,
  updating: storeState.balanceLog.updating,
  updateSuccess: storeState.balanceLog.updateSuccess
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
)(BalanceLogUpdate);
