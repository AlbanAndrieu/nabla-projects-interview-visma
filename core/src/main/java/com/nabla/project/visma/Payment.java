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

import java.io.Serializable;
import java.math.BigDecimal;

import com.nabla.project.visma.api.IPayment;

public class Payment implements IPayment, Comparable<Payment>, Serializable {

	private static final long serialVersionUID = 1L;

   /**
   * The payment loan amount.
   * @serial
   */
	private final BigDecimal loanAmount;

    // TODO add interest member

    public Payment(final BigDecimal aLoanAmount)
    {
        this.loanAmount = aLoanAmount;

        if (null == this.loanAmount)
        {
            throw new IllegalArgumentException("Loan amount cannot be null");
        }
    }

    public BigDecimal getLoanAmount()
    {
        return this.loanAmount;
    }

    @Override
    public String toString()
    {

        final StringBuilder str = new StringBuilder();

        str.append("loanAmount:").append(this.getLoanAmount());

        return str.toString();

    }

	@Override
	public int compareTo(Payment aPayment) {
		if (this == aPayment) {
			return 0;
		}

		// the object fields are never null
		final int comparison = this.loanAmount.compareTo(aPayment.loanAmount);
		if (comparison != 0) // NOPMD
		{
			return comparison;
		}

		return 0;
	}

}
