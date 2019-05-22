import './footer.scss';

import React from 'react';
import { Translate } from 'react-jhipster';
import { Col, Row } from 'reactstrap';

const Footer = props => (
  <div className="footer page-content">
    <Row>
      <Col md="12">
        <p>
          <a href="https://github.com/PavellF/loan-exchange-backend">
            <Translate contentKey="footer">footer</Translate>
          </a>
        </p>
      </Col>
    </Row>
  </div>
);

export default Footer;
