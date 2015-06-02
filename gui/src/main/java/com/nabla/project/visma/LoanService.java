/*
 * Copyright (c) 2002-2004, Nabla
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Nabla' nor 'Alban' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package com.nabla.project.visma;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nabla.project.visma.api.ILoan;
import com.nabla.project.visma.api.ILoanService;
import com.nabla.project.visma.api.IProduct;

//The @Stateless annotation eliminates the need for manual transaction
//TODO @Stateless
@Path("/loan")
public class LoanService implements ILoanService
{

    // TODO @Inject
    private static final transient Logger LOGGER = LoggerFactory.getLogger(LoanService.class);

    private BigDecimal                    loanAmount;

    private int                           numberOfYears;

    // IProduct product;

    ILoan                                 loan;

    public LoanService()
    {
        throw new AssertionError();
    }

    public LoanService(@Nonnull @Nonnegative final BigDecimal loanAmount, final int numberOfYears)
    {
        super();
        this.loanAmount = loanAmount;
        this.numberOfYears = numberOfYears;

        IProduct product = new House(this.loanAmount);
        this.loan = new HouseLoan(product, this.numberOfYears);
    }

    @Override
    @GET
    // @Produces({ MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
    public Map<Integer, List<BigDecimal>> calcMonthlyPayment()
    {

        if (null == this.loan)
        {
            throw new IllegalArgumentException("Service cannot be null");
        }

        LoanService.LOGGER.debug("Start calculateMonthlyPayment for loan amount: {} and number of years : {}", loan.getProduct().getPrice(), loan.getPaybackTime());

        return loan.calcMonthlyPayment();

    }

    @Override
    // @GET
    // @Produces({ MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
    public BigDecimal getTotalPayment()
    {

        if (null == this.loan)
        {
            throw new IllegalArgumentException("Service cannot be null");
        }

        LoanService.LOGGER.debug("Start getTotalPayment for loan amount: {} and number of years : {}", loan.getProduct().getPrice(), loan.getPaybackTime());

        return loan.getTotalPayment();
    }

    /*
     * @GET public Collection<Book> list() { return books; }
     */
}
