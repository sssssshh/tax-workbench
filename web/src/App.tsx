import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import WorkbenchPage from './pages/WorkbenchPage.tsx'
import AuditPage from './pages/AuditPage.tsx'

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      staleTime: 1000 * 30,
    },
  },
})

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<WorkbenchPage />} />
          <Route path="/audit/:id" element={<AuditPage />} />
        </Routes>
      </BrowserRouter>
    </QueryClientProvider>
  )
}

export default App