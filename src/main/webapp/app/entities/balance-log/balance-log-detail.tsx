import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './balance-log.reducer';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT } from 'app/config/constants';

export interface IBalanceLogDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class BalanceLogDetail extends React.Component<IBalanceLogDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { balanceLogEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="loanExchangeBackendApp.balanceLog.detail.title">BalanceLog</Translate> [<b>{balanceLogEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="date">
                <Translate contentKey="loanExchangeBackendApp.balanceLog.date">Date</Translate>
              </span>
            </dt>
            <dd>
              <TextFormat value={balanceLogEntity.date} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>
              <span id="oldValue">
                <Translate contentKey="loanExchangeBackendApp.balanceLog.oldValue">Old Value</Translate>
              </span>
            </dt>
            <dd>{balanceLogEntity.oldValue}</dd>
            <dt>
              <span id="amountChanged">
                <Translate contentKey="loanExchangeBackendApp.balanceLog.amountChanged">Amount Changed</Translate>
              </span>
            </dt>
            <dd>{balanceLogEntity.amountChanged}</dd>
            <dt>
              <span id="type">
                <Translate contentKey="loanExchangeBackendApp.balanceLog.type">Type</Translate>
              </span>
            </dt>
            <dd>{balanceLogEntity.type}</dd>
            <dt>
              <Translate contentKey="loanExchangeBackendApp.balanceLog.account">Account</Translate>
            </dt>
            <dd>{balanceLogEntity.account ? balanceLogEntity.account.id : ''}</dd>
            <dt>
              <Translate contentKey="loanExchangeBackendApp.balanceLog.deal">Deal</Translate>
            </dt>
            <dd>{balanceLogEntity.deal ? balanceLogEntity.deal.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/balance-log" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/balance-log/${balanceLogEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.edit">Edit</Translate>
            </span>
          </Button>
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = ({ balanceLog }: IRootState) => ({
  balanceLogEntity: balanceLog.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(BalanceLogDetail);
