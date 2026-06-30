import React from 'react';
import {AreaChart,Area,ResponsiveContainer,Tooltip,XAxis,YAxis} from 'recharts';
const money=n=>new Intl.NumberFormat('en-US',{style:'currency',currency:'USD',maximumFractionDigits:0}).format(n||0);
export default function SpendChart({data}){return <ResponsiveContainer width="100%" height={260}><AreaChart data={data}><defs><linearGradient id="g" x1="0" x2="0" y1="0" y2="1"><stop stopColor="#37d59a" stopOpacity=".45"/><stop offset="1" stopColor="#37d59a" stopOpacity="0"/></linearGradient></defs><XAxis dataKey="month"/><YAxis tickFormatter={x=>`$${x}`}/><Tooltip formatter={x=>money(x)}/><Area type="monotone" dataKey="spend" stroke="#37d59a" strokeWidth={3} fill="url(#g)"/></AreaChart></ResponsiveContainer>}
