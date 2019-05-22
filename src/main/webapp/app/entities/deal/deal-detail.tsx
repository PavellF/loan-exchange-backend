import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './deal.reducer';
import { IDeal } from 'app/shared/model/deal.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IDealDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class DealDetail extends React.Component<IDealDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { dealEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="loanExchangeBackendApp.deal.detail.title">Deal</Translate> [<b>{dealEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="dateOpen">
                <Translate contentKey="loanExchangeBackendApp.deal.dateOpen">Date Open</Translate>
              </span>
            </dt>
            <dd>
              <TextFormat value={dealEntity.dateOpen} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>
              <span id="dateBecomeActive">
                <Translate contentKey="loanExchangeBackendApp.deal.dateBecomeActive">Date Become Active</Translate>
              </span>
            </dt>
            <dd>
              <TextFormat value={dealEntity.dateBecomeActive} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>
              <span id="endDate">
                <Translate contentKey="loanExchangeBackendApp.deal.endDate">End Date</Translate>
              </span>
            </dt>
            <dd>
              <TextFormat value={dealEntity.endDate} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>
              <span id="startBalance">
                <Translate contentKey="loanExchangeBackendApp.deal.startBalance">Start Balance</Translate>
              </span>
            </dt>
            <dd>{dealEntity.startBalance}</dd>
            <dt>
              <span id="percent">
                <Translate contentKey="loanExchangeBackendApp.deal.percent">Percent</Translate>
              </span>
            </dt>
            <dd>{dealEntity.percent}</dd>
            <dt>
              <span id="successRate">
                <Translate contentKey="loanExchangeBackendApp.deal.successRate">Success Rate</Translate>
              </span>
            </dt>
            <dd>{dealEntity.successRate}</dd>
            <dt>
              <span id="term">
                <Translate contentKey="loanExchangeBackendApp.deal.term">Term</Translate>
              </span>
            </dt>
            <dd>{dealEntity.term}</dd>
            <dt>
              <span id="paymentEvery">
                <Translate contentKey="loanExchangeBackendApp.deal.paymentEvery">Payment Every</Translate>
              </span>
            </dt>
            <dd>{dealEntity.paymentEvery}</dd>
            <dt>
              <span id="status">
                <Translate contentKey="loanExchangeBackendApp.deal.status">Status</Translate>
              </span>
            </dt>
            <dd>{dealEntity.status}</dd>
            <dt>
              <Translate contentKey="loanExchangeBackendApp.deal.emitter">Emitter</Translate>
            </dt>
            <dd>{dealEntity.emitter ? dealEntity.emitter.id : ''}</dd>
            <dt>
              <Translate contentKey="loanExchangeBackendApp.deal.recipient">Recipient</Translate>
            </dt>
            <dd>{dealEntity.recipient ? dealEntity.recipient.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/deal" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/deal/${dealEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ deal }: IRootState) => ({
  dealEntity: deal.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(DealDetail);
