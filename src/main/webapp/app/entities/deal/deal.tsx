import React from 'react';
import InfiniteScroll from 'react-infinite-scroller';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { getSortState, IPaginationBaseState, TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntities, reset } from './deal.reducer';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';

export interface IDealProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export type IDealState = IPaginationBaseState;

export class Deal extends React.Component<IDealProps, IDealState> {
  state: IDealState = {
    ...getSortState(this.props.location, ITEMS_PER_PAGE)
  };

  componentDidMount() {
    this.reset();
  }

  componentDidUpdate() {
    if (this.props.updateSuccess) {
      this.reset();
    }
  }

  reset = () => {
    this.props.reset();
    this.setState({ activePage: 1 }, () => {
      this.getEntities();
    });
  };

  handleLoadMore = () => {
    if (window.pageYOffset > 0) {
      this.setState({ activePage: this.state.activePage + 1 }, () => this.getEntities());
    }
  };

  sort = prop => () => {
    this.setState(
      {
        order: this.state.order === 'asc' ? 'desc' : 'asc',
        sort: prop
      },
      () => {
        this.reset();
      }
    );
  };

  getEntities = () => {
    const { activePage, itemsPerPage, sort, order } = this.state;
    this.props.getEntities(activePage - 1, itemsPerPage, `${sort},${order}`);
  };

  render() {
    const { dealList, match } = this.props;
    return (
      <div>
        <h2 id="deal-heading">
          <Translate contentKey="loanExchangeBackendApp.deal.home.title">Deals</Translate>
          <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="loanExchangeBackendApp.deal.home.createLabel">Create new Deal</Translate>
          </Link>
        </h2>
        <div className="table-responsive">
          <InfiniteScroll
            pageStart={this.state.activePage}
            loadMore={this.handleLoadMore}
            hasMore={this.state.activePage - 1 < this.props.links.next}
            loader={<div className="loader">Loading ...</div>}
            threshold={0}
            initialLoad={false}
          >
            <Table responsive>
              <thead>
                <tr>
                  <th className="hand" onClick={this.sort('id')}>
                    <Translate contentKey="global.field.id">ID</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('dateOpen')}>
                    <Translate contentKey="loanExchangeBackendApp.deal.dateOpen">Date Open</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('dateBecomeActive')}>
                    <Translate contentKey="loanExchangeBackendApp.deal.dateBecomeActive">Date Become Active</Translate>{' '}
                    <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('startBalance')}>
                    <Translate contentKey="loanExchangeBackendApp.deal.startBalance">Start Balance</Translate>{' '}
                    <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('percent')}>
                    <Translate contentKey="loanExchangeBackendApp.deal.percent">Percent</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('fine')}>
                    <Translate contentKey="loanExchangeBackendApp.deal.fine">Fine</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('successRate')}>
                    <Translate contentKey="loanExchangeBackendApp.deal.successRate">Success Rate</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('term')}>
                    <Translate contentKey="loanExchangeBackendApp.deal.term">Term</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('paymentEvery')}>
                    <Translate contentKey="loanExchangeBackendApp.deal.paymentEvery">Payment Every</Translate>{' '}
                    <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('status')}>
                    <Translate contentKey="loanExchangeBackendApp.deal.status">Status</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('autoPaymentEnabled')}>
                    <Translate contentKey="loanExchangeBackendApp.deal.autoPaymentEnabled">Auto Payment Enabled</Translate>{' '}
                    <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('capitalization')}>
                    <Translate contentKey="loanExchangeBackendApp.deal.capitalization">Capitalization</Translate>{' '}
                    <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('earlyPayment')}>
                    <Translate contentKey="loanExchangeBackendApp.deal.earlyPayment">Early Payment</Translate>{' '}
                    <FontAwesomeIcon icon="sort" />
                  </th>
                  <th>
                    <Translate contentKey="loanExchangeBackendApp.deal.emitter">Emitter</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th>
                    <Translate contentKey="loanExchangeBackendApp.deal.recipient">Recipient</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {dealList.map((deal, i) => (
                  <tr key={`entity-${i}`}>
                    <td>
                      <Button tag={Link} to={`${match.url}/${deal.id}`} color="link" size="sm">
                        {deal.id}
                      </Button>
                    </td>
                    <td>
                      <TextFormat type="date" value={deal.dateOpen} format={APP_DATE_FORMAT} />
                    </td>
                    <td>
                      <TextFormat type="date" value={deal.dateBecomeActive} format={APP_DATE_FORMAT} />
                    </td>
                    <td>{deal.startBalance}</td>
                    <td>{deal.percent}</td>
                    <td>{deal.fine}</td>
                    <td>{deal.successRate}</td>
                    <td>{deal.term}</td>
                    <td>
                      <Translate contentKey={`loanExchangeBackendApp.Period.${deal.paymentEvery}`} />
                    </td>
                    <td>
                      <Translate contentKey={`loanExchangeBackendApp.DealStatus.${deal.status}`} />
                    </td>
                    <td>{deal.autoPaymentEnabled ? 'true' : 'false'}</td>
                    <td>{deal.capitalization ? 'true' : 'false'}</td>
                    <td>{deal.earlyPayment ? 'true' : 'false'}</td>
                    <td>{deal.emitter ? deal.emitter.id : ''}</td>
                    <td>{deal.recipient ? deal.recipient.id : ''}</td>
                    <td className="text-right">
                      <div className="btn-group flex-btn-group-container">
                        <Button tag={Link} to={`${match.url}/${deal.id}`} color="info" size="sm">
                          <FontAwesomeIcon icon="eye" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.view">View</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`${match.url}/${deal.id}/edit`} color="primary" size="sm">
                          <FontAwesomeIcon icon="pencil-alt" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.edit">Edit</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`${match.url}/${deal.id}/delete`} color="danger" size="sm">
                          <FontAwesomeIcon icon="trash" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.delete">Delete</Translate>
                          </span>
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          </InfiniteScroll>
        </div>
      </div>
    );
  }
}

const mapStateToProps = ({ deal }: IRootState) => ({
  dealList: deal.entities,
  totalItems: deal.totalItems,
  links: deal.links,
  entity: deal.entity,
  updateSuccess: deal.updateSuccess
});

const mapDispatchToProps = {
  getEntities,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Deal);
