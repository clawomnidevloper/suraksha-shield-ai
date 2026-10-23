package com.example.data.local

import kotlinx.coroutines.flow.Flow

interface ScamReportDao {
    fun getAllReports(): Flow<List<ScamReportEntity>>
    fun searchReports(query: String): Flow<List<ScamReportEntity>>
    fun getReportsByType(type: String): Flow<List<ScamReportEntity>>
    suspend fun findExactMatch(target: String): ScamReportEntity?
    suspend fun findPartialMatch(target: String): ScamReportEntity?
    suspend fun insertReport(report: ScamReportEntity): Long
    suspend fun insertReports(reports: List<ScamReportEntity>)
    suspend fun upvoteReport(id: Long)
    suspend fun getCount(): Int
}
