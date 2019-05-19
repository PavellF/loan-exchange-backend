import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './notification.reducer';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT } from 'app/config/constants';

export interface INotificationDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class NotificationDetail extends React.Component<INotificationDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { notificationEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="loanExchangeBackendApp.notification.detail.title">Notification</Translate> [
            <b>{notificationEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="haveRead">
                <Translate contentKey="loanExchangeBackendApp.notification.haveRead">Have Read</Translate>
              </span>
            </dt>
            <dd>{notificationEntity.haveRead ? 'true' : 'false'}</dd>
            <dt>
              <span id="date">
                <Translate contentKey="loanExchangeBackendApp.notification.date">Date</Translate>
              </span>
            </dt>
            <dd>
              <TextFormat value={notificationEntity.date} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>
              <span id="message">
                <Translate contentKey="loanExchangeBackendApp.notification.message">Message</Translate>
              </span>
            </dt>
            <dd>{notificationEntity.message}</dd>
            <dt>
              <Translate contentKey="loanExchangeBackendApp.notification.recipient">Recipient</Translate>
            </dt>
            <dd>{notificationEntity.recipient ? notificationEntity.recipient.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/notification" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/notification/${notificationEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ notification }: IRootState) => ({
  notificationEntity: notification.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(NotificationDetail);
